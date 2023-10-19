package dio.desafiosantanderdevweek2023.repository;

import dio.desafiosantanderdevweek2023.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

		boolean existsByAccountNumber(String accountNumber);

		boolean existsByCardNumber(String number);
}
