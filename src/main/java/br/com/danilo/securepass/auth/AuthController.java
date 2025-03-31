package br.com.danilo.securepass.auth;

import br.com.danilo.securepass.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/login")
    public Token login(@RequestBody Credentials credentials) {

        var user = userRepository.findByUsername(credentials.username())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não foi possível ser encontrado."));

        if ( !passwordEncoder.matches(credentials.password(), user.getPassword()) ) {
            throw new BadCredentialsException("Senha incorreta");
        }

        return tokenService.create(user);
    }
}
