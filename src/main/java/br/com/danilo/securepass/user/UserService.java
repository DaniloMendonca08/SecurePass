package br.com.danilo.securepass.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(User user) {
        log.info("Registrando novo usuário...");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User createdUser = userRepository.save(user);

        log.info("Usuário registrado com sucesso.");
        return createdUser;
    }

    public boolean delete(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            log.info("Usuário deletado com sucesso.");
            return true;
        }
        log.warn("Não foi possível excluir este usuário.");
        return false;
    }

    public User getUserInfo(String username) {
        log.debug("Buscando informações do usuário...");
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não foi encontrado na base de dados!"));
    }

    public User update(UpdateUserDTO updateUserDTO, String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não foi encontrado na base de dados!"));

        user.setName(updateUserDTO.getName());
        user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));

        User updatedUser = userRepository.save(user);

        log.info("Usuário atualizado com sucesso.");
        return updatedUser;
    }
}
