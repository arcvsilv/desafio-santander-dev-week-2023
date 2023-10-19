package dio.desafiosantanderdevweek2023.controller;

import dio.desafiosantanderdevweek2023.dto.UserDto;
import dio.desafiosantanderdevweek2023.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/users")
@Tag(name = "Users Controller", description = "RESTful API for managing users.")
public record UserController(UserService userService) {
		@GetMapping
		@Operation(summary = "Obter todos usuários", description = "Recuperar uma lista de todos os usuários registrados")
		@ApiResponses(value = {
						@ApiResponse(responseCode = "200", description = "Operação concluída com sucesso")
		})
		public ResponseEntity<List<UserDto>> findAll() {
				var users = userService.findAll();
				var usersDto = users.stream().map(UserDto::new).collect(Collectors.toList());
				return ResponseEntity.ok(usersDto);
		}

		@GetMapping("/{id}")
		@Operation(summary = "Obter usuário por ID", description = "Recuperar um usuário específico com baseado no ID")
		@ApiResponses(value = {
						@ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
						@ApiResponse(responseCode = "404", description = "User not found")
		})
		public ResponseEntity<UserDto> findById(@PathVariable Long id) {
				var user = userService.findById(id);
				return ResponseEntity.ok(new UserDto(user));
		}

		@PostMapping
		@Operation(summary = "Criação de novo usuário", description = "Criação novo usuário e retorna o corpo do novo usuário")
		@ApiResponses(value = {
						@ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
						@ApiResponse(responseCode = "422", description = "Dados de usuário fornecidos inválidos")
		})
		public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
				var userCreated = userService.create(userDto.toModel());
				URI location = ServletUriComponentsBuilder.fromCurrentRequest()
								.path("/{id}")
								.buildAndExpand(userCreated.getId())
								.toUri();
				return ResponseEntity.created(location).body(new UserDto(userCreated));
		}

		@PutMapping("/{id}")
		@Operation(summary = "Alteração de usuário", description = "Atualizar os dados de um usuário existente com base em seu ID")
		@ApiResponses(value = {
						@ApiResponse(responseCode = "200", description = "Usuário alterado com sucesso"),
						@ApiResponse(responseCode = "404", description = "User not found"),
						@ApiResponse(responseCode = "422", description = "Dados de usuário fornecidos inválidos")
		})
		public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserDto userDto) {
				var user = userService.update(id, userDto.toModel());
				return ResponseEntity.ok(new UserDto(user));
		}

		@DeleteMapping("/{id}")
		@Operation(summary = "Delete a user", description = "Delete an existing user based on its ID")
		@ApiResponses(value = {
						@ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
						@ApiResponse(responseCode = "404", description = "User not found")
		})
		public ResponseEntity<Void> delete(@PathVariable Long id){
				userService.delete(id);
				return ResponseEntity.noContent().build();
		}
}