package br.com.danilo.securepass.user;

import br.com.danilo.securepass.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
        @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuário criado com sucesso."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Não foi possível realizar a criação do usuário.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        })
    @PostMapping
    public ResponseEntity<User> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados para criação de um novo usuário",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Exemplo de criação de usuário",
                    value = """
                {
                  "name": "João da Silva",
                  "username": "JoaoSilva",
                  "password": "SenhaForte123#",
                  "birthDate": "2002-09-19"
                }
                """
                )
            )
        )
        @Valid @RequestBody User user,
        UriComponentsBuilder uriBuilder
    ) {

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
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuário excluído com sucesso."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuário não foi encontrado para exclusão."),
    })
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
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuário encontrado e retornado com sucesso."),
    })
    @GetMapping("/info")
    public ResponseEntity<User> getInfo(@AuthenticationPrincipal String username) {
        log.debug("Requisição recebida para buscar as informações do usuário logado.");

        User user = userService.getUserInfo(username);

        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "Atualiza dados do usuário",
        description = """
            Com os dados recebidos no corpo da requisição, atualiza o usuário no sistema e retorna suas informações atualizadas. \s
             \s
             É possível atualizar apenas o nome e a senha do usuário."""
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dados do usuário atualizados com sucesso."),

        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Dados inválidos para atualização do usuário.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),

        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "O token fornecido é inválido.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
    })
    @PutMapping("/update")
    public ResponseEntity<User> update(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados para atualização de um usuário",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Exemplo de atualização de dados do usuário",
                    value = "{\n  \"name\": \"João da Silva Junior\",\n  \"password\": \"SenhaForte225#\"\n}"
                )
            )
        )
        @Valid @RequestBody UpdateUserDTO updateUserDTO,
        @AuthenticationPrincipal String username
    ) {
        log.debug("Requisição recebida para atualizar os dados do usuário.");

        var updatedUser = userService.update(updateUserDTO,username);

        return ResponseEntity.ok(updatedUser);

    }
}
