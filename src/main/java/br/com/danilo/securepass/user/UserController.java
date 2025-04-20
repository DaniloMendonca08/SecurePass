package br.com.danilo.securepass.user;

import br.com.danilo.securepass.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user, UriComponentsBuilder uriBuilder) {
        log.info("Requisição recebida para criar um novo usuário.");

        User createdUser = userService.create(user);

        var uri = uriBuilder
                .path("/user/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(createdUser);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        log.info("Requisição recebida para deletar um usuário baseado em seu ID.");

        if (userService.delete(id)) {
            return ResponseEntity.ok(new ApiResponse("Usuário excluido com sucesso."));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse("Usuário não foi encontrado para exclusão."));
    }

    @GetMapping("/info")
    public ResponseEntity<User> getInfo(@AuthenticationPrincipal String username) {
        log.debug("Requisição recebida para buscar as informações do usuário logado.");

        User user = userService.getUserInfo(username);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<User> update(@Valid @RequestBody UpdateUserDTO updateUserDTO, @AuthenticationPrincipal String username) {
        log.debug("Requisição recebida para atualizar os dados do usuário.");

        var updatedUser = userService.update(updateUserDTO,username);

        return ResponseEntity.ok(updatedUser);

    }
}
