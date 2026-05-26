package io.github.fbsarracini.javadesign.auth;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final AuthenticateUser authenticateUser;

    public LoginController(AuthenticateUser authenticateUser) {
        this.authenticateUser = authenticateUser;
    }

    @PostMapping("/api/auth/login")
    public LoginResponse execute(@RequestBody @Valid LoginRequest request) {
        String token = authenticateUser.execute(request.email(), request.password());
        return LoginResponse.of(token);
    }
}
