package br.com.danilo.securepass.auth;

import br.com.danilo.securepass.user.User;
import br.com.danilo.securepass.user.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private final UserRepository userRepository;
    private final Algorithm algorithm;

    public TokenService(UserRepository userRepository, @Value("${jwt.secret}") String secret) {
        this.userRepository = userRepository;
        algorithm = Algorithm.HMAC256(secret);
    }

    public Token create(User user) {

        var expiresAt = LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.ofHours(-3));
        String token = JWT.create()
                .withIssuer("SecurePass")
                .withSubject(user.getUsername())
                .withClaim("role", "user")
                .withExpiresAt(expiresAt)
                .sign(algorithm);

        Token createdToken = new Token(token);

        log.debug("Token gerado com sucesso.");
        return createdToken;
    }

    public User getUserFromToken(String token) {
        var username = JWT.require(algorithm)
                .withIssuer("SecurePass")
                .build()
                .verify(token)
                .getSubject();

        User userFound = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não foi encontrado."));
    }
}
