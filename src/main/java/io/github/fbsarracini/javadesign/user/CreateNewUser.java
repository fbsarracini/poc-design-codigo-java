package io.github.fbsarracini.javadesign.user;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.exception.UnprocessableException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@UseCase
public class CreateNewUser {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateNewUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(NewUserData newUserData) {
        userRepository.findByEmail(newUserData.getEmail())
                .ifPresent(u -> { throw new UnprocessableException(); });

        return userRepository.save(newUserData.toNewUser(passwordEncoder));
    }
}
