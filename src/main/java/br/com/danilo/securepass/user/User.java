package br.com.danilo.securepass.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
// Utilizando esse nome de tabela para evitar conflitos com o banco de dados
@Table(name = "users")
@Data
public class User {

    @Id
    // Será utilizado uma sequence no banco de dados para manipulação dos IDs de forma automática
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")

    // A sequence será apelidada de user_seq para o Hibernate gerenciar internamente
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    Long id;

    @NotBlank(message = "O nome não deve ser nulo e nem conter espaços em branco.")
    String name;

    @NotBlank(message = "O nome de usuário não pode ser nulo e também não pode conter espaços em branco.")
    String username;

    @NotBlank(message = "A senha não pode ser nula.")
    @Size(min = 8, message = "A senha deve conter no mínimo 8 caracteres.")

    // Utilizando um regexp para garantir que a senha fornecida seja mais segura, contendo letra, número e caractere especial
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "A senha deve conter pelo menos uma letra maiúscula, um número e um caractere especial"
    )
    String password;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser válida.")
    LocalDate birthDate;

    LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


}
