package br.com.danilo.securepass.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private  UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private String encodedPassword;
    private UpdateUserDTO updateUserDTO;
    private String usernameInexistente;

    @BeforeEach
    public void setUp() {
        user = new User
        (1L,
        "Matheus Silva",
        "MatheusSS09",
        "Teste803#",
        LocalDate.parse("2003-11-14"),
        LocalDateTime.parse("2025-04-07T10:00:00")
        );

        encodedPassword = "senhaEncoded123";

        updateUserDTO = new UpdateUserDTO("UserDTO", "Userdto#123");

        usernameInexistente = "UserNaoCadastradoNoBanco";
    }

    @DisplayName("Deve criar um usuário quando os dados forem válidos")
    @Test
    public void deveCriarUsuario_QuandoDadosValidos() {
        Mockito.when(passwordEncoder.encode(Mockito.anyString()))
            .thenReturn(encodedPassword);

        Mockito.when(userRepository.save(Mockito.any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.create(user);

        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals("Matheus Silva", createdUser.getName());
        Assertions.assertEquals("MatheusSS09", createdUser.getUsername());
        Assertions.assertEquals(encodedPassword, createdUser.getPassword());
        Assertions.assertEquals(LocalDate.parse("2003-11-14"), createdUser.getBirthDate());
        Assertions.assertEquals(LocalDateTime.parse("2025-04-07T10:00:00"), createdUser.getCreatedAt());

        Mockito.verify(passwordEncoder).encode("Teste803#");
        Mockito.verify(userRepository).save(Mockito.any(User.class));

    }


    @DisplayName("Deve deletar um usuário caso seja encontrado o ID")
    @Test
    public void deveDeletarUsuario_CasoEncontradoID() {
        Mockito.when(userRepository.findById(user.getId()))
            .thenReturn(Optional.of(user));

        boolean resultado = userService.delete(user.getId());

        Assertions.assertTrue(resultado);
        Mockito.verify(userRepository).deleteById(user.getId());
    }

    @DisplayName("Não deve deletar um usuário caso o ID não seja encontrado")
    @Test
    public void naoDeveDeletarUsuario_CasoIdNaoEncontrado() {
        Mockito.when(userRepository.findById(user.getId()))
            .thenReturn(Optional.empty());

        boolean resultado = userService.delete(user.getId());

        Assertions.assertFalse(resultado);
        Mockito.verify(userRepository, Mockito.never())
            .deleteById(user.getId());
    }

    @DisplayName("Deve retornar informações do usuário quando encontrar o username")
    @Test
    public void deveRetornarInformacoes_QuandoUsernameExistir() {
        Mockito.when(userRepository.findByUsername(user.getUsername()))
            .thenReturn(Optional.of(user));

        User userEncontrado = userService.getUserInfo(user.getUsername());

        Assertions.assertNotNull(userEncontrado);
        Mockito.verify(userRepository).findByUsername(user.getUsername());
    }

    @DisplayName("Não deve retornar informações do usuário quando username não existir")
    @Test
    public void naoDeveRetornarInformacos_QuandoUsernameNaoExistir() {
        Mockito.when(userRepository.findByUsername(usernameInexistente))
            .thenReturn(Optional.empty());

        UsernameNotFoundException exception = Assertions.assertThrows(
            UsernameNotFoundException.class,
            () -> userService.getUserInfo(usernameInexistente)
        );

        Assertions.assertEquals("Usuário não foi encontrado na base de dados!", exception.getMessage());

        Mockito.verify(userRepository).findByUsername(usernameInexistente);

    }

    @DisplayName("Deve atualizar dados do usuário quando o username for válido")
    @Test
    public void deveAtualizarUsuario_QuandoUsernameExistir() {
        Mockito.when(userRepository.findByUsername(user.getUsername()))
            .thenReturn(Optional.of(user));

        Mockito.when(passwordEncoder.encode(updateUserDTO.getPassword()))
            .thenReturn(encodedPassword);

        Mockito.when(userRepository.save(Mockito.any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        User usuarioAtualizado = userService.update(updateUserDTO, user.getUsername());

        Assertions.assertNotNull(usuarioAtualizado);
        Assertions.assertEquals(updateUserDTO.getName(), usuarioAtualizado.getName());
        Assertions.assertEquals(encodedPassword, usuarioAtualizado.getPassword());

        Mockito.verify(userRepository).findByUsername(user.getUsername());
        Mockito.verify(passwordEncoder).encode(updateUserDTO.getPassword());
        Mockito.verify(userRepository).save(Mockito.any(User.class));

    }

    @DisplayName("Não deve atualizar usuário quando o username não existir")
    @Test
    public void naoDeveAtualizarUsuario_QuandoUsernameNaoExistir() {
        Mockito.when(userRepository.findByUsername(usernameInexistente))
            .thenReturn(Optional.empty());

        UsernameNotFoundException exception = Assertions.assertThrows(
            UsernameNotFoundException.class,
            () -> userService.update(updateUserDTO, usernameInexistente)
        );

        Assertions.assertEquals("Usuário não foi encontrado na base de dados!", exception.getMessage());

        Mockito.verify(userRepository).findByUsername(usernameInexistente);
    }





}