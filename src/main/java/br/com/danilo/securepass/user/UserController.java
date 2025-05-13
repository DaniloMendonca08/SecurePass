package br.com.danilo.securepass.user;

import br.com.danilo.securepass.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Gerenciamento de usuários", description = "Endpoints para criação, atualização, exclusão e consulta de informações dos usuários.")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation
        (
            summary = "Cria um novo usuário",
            description = "Recebe os dados do usuário no corpo da requisição, cria o usuário e retorna o recurso criado."
        )
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

    @Operation
        (
            summary = "Exclui um usuário existente",
            description = "Realiza a exclusão de um usuário com base no ID fornecido. Retorna uma resposta indicando se teve sucesso ou falha na execução."
        )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        log.info("Requisição recebida para deletar um usuário baseado em seu ID.");

        if (userService.delete(id)) {
            return ResponseEntity.ok(new ApiResponse("Usuário excluido com sucesso."));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse("Usuário não foi encontrado para exclusão."));
    }

    @Operation(
        summary = "Busca informações do usuário logado",
        description = "Retorna os dados do usuário autenticado."
    )
    @GetMapping("/info")
    public ResponseEntity<User> getInfo(@AuthenticationPrincipal String username) {
        log.debug("Requisição recebida para buscar as informações do usuário logado.");

        User user = userService.getUserInfo(username);

        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "Atualiza dados do usuário",
        description = "Com os dados recebidos no corpo da requisição, atualiza o usuário no sistema e retorna suas informações atualizadas."
    )
    @PutMapping("/update")
    public ResponseEntity<User> update(@Valid @RequestBody UpdateUserDTO updateUserDTO, @AuthenticationPrincipal String username) {
        log.debug("Requisição recebida para atualizar os dados do usuário.");

        var updatedUser = userService.update(updateUserDTO,username);

        return ResponseEntity.ok(updatedUser);

    }
}
