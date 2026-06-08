package io.github.fbsarracini.javadesign.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;

public record NewUserRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 12) String password
) implements @Valid NewUserData {

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public User toNewUser(PasswordEncoder passwordEncoder) {
        return new User(name, email, passwordEncoder.encode(password));
    }
}
