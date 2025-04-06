package br.com.danilo.securepass.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {

    @NotBlank(message = "O nome não deve ser nulo e nem conter espaços em branco.")
    private String name;

    @NotBlank(message = "A senha não pode ser nula.")
    @Pattern(
            regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
            message = "A senha deve conter pelo menos 8 caracteres, dentre eles uma letra maiúscula, uma letra minúscula, um número e um caractere especial."
    )
    private String password;
}
