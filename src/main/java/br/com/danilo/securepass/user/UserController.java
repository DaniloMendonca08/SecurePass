package br.com.danilo.securepass.user;

import br.com.danilo.securepass.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user, UriComponentsBuilder uriBuilder) {
        User createdUser = userService.create(user);

        var uri = uriBuilder
                .path("/user/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(createdUser);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        if (userService.delete(id)) {
            return ResponseEntity.ok(new ApiResponse("Usuário excluido com sucesso."));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse("Usuário não foi encontrado para exclusão."));
    }

    @GetMapping("/info")
    public ResponseEntity<User> getInfo(@AuthenticationPrincipal String username) {
        User user = userService.getUserInfo(username);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<User> update(@Valid @RequestBody UpdateUserDTO UpdateUserDTO, @AuthenticationPrincipal String username) {
        var UpdatedUser = userService.update(UpdateUserDTO,username);

        return ResponseEntity.ok(UpdatedUser);

    }
}
