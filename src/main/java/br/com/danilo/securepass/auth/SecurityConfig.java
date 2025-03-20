package br.com.danilo.securepass.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// É usada para indicar que a classe terá configurações e definição de beans
// para a aplicação
@Configuration
public class SecurityConfig {

    // O Spring vai criar e armazenar esse objeto no contexto da aplicação
    // permitindo injeção de dependências, evitando a necessidade
    // de instanciar manualmente a classe quando for utilizada.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
