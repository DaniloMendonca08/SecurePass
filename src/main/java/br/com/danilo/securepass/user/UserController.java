package br.com.danilo.securepass.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
