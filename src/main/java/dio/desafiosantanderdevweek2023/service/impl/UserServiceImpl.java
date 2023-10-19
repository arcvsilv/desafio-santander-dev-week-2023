package dio.desafiosantanderdevweek2023.service.impl;

import dio.desafiosantanderdevweek2023.model.User;
import dio.desafiosantanderdevweek2023.repository.UserRepository;
import dio.desafiosantanderdevweek2023.service.*;
import dio.desafiosantanderdevweek2023.service.exception.BusinessException;
import dio.desafiosantanderdevweek2023.service.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Optional.ofNullable;

@Service
public class UserServiceImpl implements UserService {
		private static final Long UNCHANGEABLE_USER_ID = 1L;

		private final UserRepository userRepository;

		public UserServiceImpl(UserRepository userRepository) {
				this.userRepository = userRepository;
		}

		@Transactional(readOnly = true)
		public List<User> findAll() {
				return this.userRepository.findAll();
		}

		@Transactional(readOnly = true)
		public User findById(Long id) {
				return this.userRepository.findById(id).orElseThrow(NotFoundException::new);
		}

		@Transactional
		public User create(User userToCreate) {
				ofNullable(userToCreate).orElseThrow(() -> new BusinessException("User to create must not be null."));
				ofNullable(userToCreate.getAccount()).orElseThrow(() -> new BusinessException("User account must not be null."));
				ofNullable(userToCreate.getCard()).orElseThrow(() -> new BusinessException("User card must not be null."));

				this.validateChangeableId(userToCreate.getId(), "created");
				if (userRepository.existsByAccountNumber(userToCreate.getAccount().getNumber())) {
						throw new BusinessException("This account number already exists.");
				}
				if (userRepository.existsByCardNumber(userToCreate.getCard().getNumber())) {
						throw new BusinessException("This card number already exists.");
				}
				return this.userRepository.save(userToCreate);
		}

		@Transactional
		public User update(Long id, User userToUpdate) {
				this.validateChangeableId(id, "updated");
				User dbUser = this.findById(id);
				if (!dbUser.getId().equals(userToUpdate.getId())) {
						throw new BusinessException("Update IDs must be the same.");
				}

				dbUser.setName(userToUpdate.getName());
				dbUser.setAccount(userToUpdate.getAccount());
				dbUser.setCard(userToUpdate.getCard());
				dbUser.setFeatures(userToUpdate.getFeatures());
				dbUser.setNews(userToUpdate.getNews());

				return this.userRepository.save(dbUser);
		}

		@Transactional
		public void delete(Long id) {
				this.validateChangeableId(id, "deleted");
				User dbUser = this.findById(id);
				this.userRepository.delete(dbUser);
		}

		private void validateChangeableId(Long id, String operation) {
				if (UNCHANGEABLE_USER_ID.equals(id)) {
						throw new BusinessException("User with ID %d can not be %s.".formatted(UNCHANGEABLE_USER_ID, operation));
				}
		}
}