package br.com.danilo.securepass.auth;

import br.com.danilo.securepass.response.ApiResponse;
import br.com.danilo.securepass.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação do usuário", description = "Endpoint responsável por realizar a autenticação de usuários.")
@Slf4j
@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Operation(
        summary = "Realiza login do usuário",
        description = "Realiza login do usuário e retorna um token de acesso em caso de sucesso."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login realizado com sucesso."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Usuário ou senha está incorreto.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
    })
    @PostMapping("/login")
    public Token login(@RequestBody Credentials credentials) {
        log.debug("Realizando login do usuário...");

        var user = userRepository.findByUsername(credentials.username())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não foi possível ser encontrado."));

        if ( !passwordEncoder.matches(credentials.password(), user.getPassword()) ) {
            throw new BadCredentialsException("Senha incorreta");
        }

        return tokenService.create(user);
    }
}
