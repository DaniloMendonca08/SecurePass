package br.com.danilo.securepass.auth;

import br.com.danilo.securepass.user.User;
import br.com.danilo.securepass.user.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    private Algorithm algorithm;

    private String secret;

    private User user;

    @BeforeEach
    public void setUp() {
        secret = "nmkdscauhiodfasbjkdhaS03489550437+_)#+";

        tokenService = new TokenService(userRepository, secret);

        user = new User
        (1L,
        "Matheus Silva",
        "MatheusSS09",
        "Teste803#",
        LocalDate.parse("2003-11-14"),
        LocalDateTime.parse("2025-04-07T10:00:00")
        );

        algorithm = Algorithm.HMAC256(secret);
    }

    @DisplayName("Deve criar um token válido")
    @Test
    public void deveCriarToken_Valido() {
        Token generatedToken = tokenService.create(user);

        Assertions.assertNotNull(generatedToken);

        DecodedJWT tokenDecodificado = JWT.require(algorithm)
                                          .withIssuer("SecurePass")
                                          .build()
                                          .verify(generatedToken.token());

        Assertions.assertEquals(user.getUsername(), tokenDecodificado.getSubject());
        Assertions.assertEquals("user", tokenDecodificado.getClaim("role").asString());
    }

    @DisplayName("Deve retornar um usuário quando o token for válido")
    @Test
    public void deveRetornarUsuario_QuandoTokenValido() {
        Token generatedToken = tokenService.create(user);

        Mockito.when(userRepository.findByUsername(user.getUsername()))
          .thenReturn(Optional.of(user));

        User userEncontrado = tokenService.getUserFromToken(generatedToken.token());

        Assertions.assertNotNull(userEncontrado);
        Assertions.assertEquals(user.getUsername(), userEncontrado.getUsername());
        Mockito.verify(userRepository).findByUsername(user.getUsername());
    }

    @DisplayName("Não deve retornar usuário quando username do token não existir no banco")
    @Test
    public void naoDeveRetornarUsuario_QuandoUsernameDoTokenNaoExistir () {
        Token token = tokenService.create(user);

        Mockito.when(userRepository.findByUsername(user.getUsername()))
          .thenReturn(Optional.empty());

        UsernameNotFoundException exception = Assertions.assertThrows(
          UsernameNotFoundException.class,
          () -> tokenService.getUserFromToken(token.token())
        );

        Assertions.assertEquals("Usuário não foi encontrado.", exception.getMessage());
        Mockito.verify(userRepository).findByUsername(user.getUsername());
    }

    @DisplayName("Não deve validar o token quando o emissor for inválido")
    @Test
    public void naoDeveValidarToken_QuandoEmissorForInvalido() {
        String token = JWT.create()
            .withIssuer("IssuerInvalido")
            .withSubject(user.getUsername())
            .withClaim("role", "user")
            .withExpiresAt(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.ofHours(-3)))
            .sign(algorithm);

        JWTVerificationException exception = Assertions.assertThrows(
            JWTVerificationException.class,
            () -> tokenService.getUserFromToken(token)
        );

        Assertions.assertTrue(exception.getMessage().contains("The Claim 'iss' value doesn't match the required issuer."));
    }

}