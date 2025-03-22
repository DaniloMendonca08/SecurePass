package br.com.danilo.securepass.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// É usada para indicar que a classe terá configurações e definição de beans
// para a aplicação
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain config(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth ->
                auth
                        .requestMatchers(HttpMethod.POST, "/user").permitAll()
                        .anyRequest().authenticated()

        );

        return http.build();
    }

    // O Spring vai criar e armazenar esse objeto no contexto da aplicação
    // permitindo injeção de dependências, evitando a necessidade
    // de instanciar manualmente a classe quando for utilizada.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
