package io.github.fbsarracini.javadesign.user;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.exception.UnprocessableException;
import io.github.fbsarracini.javadesign.log.AppLog;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@UseCase
public class CreateNewUser {

    private static final Logger log = LoggerFactory.getLogger(CreateNewUser.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateNewUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(@Valid NewUserData newUserData) {
        AppLog.logger(log).system().does("criar usuário").on("email=" + newUserData.getEmail()).debug();
        userRepository.findByEmail(newUserData.getEmail())
                .ifPresent(u -> {
                    AppLog.logger(log).system().does("criar usuário").on("email=" + newUserData.getEmail()).failed("email já cadastrado").warn();
                    throw new UnprocessableException();
                });

        AppLog.logger(log).system().does("criar usuário").on("email=" + newUserData.getEmail()).info();
        User user = userRepository.save(newUserData.toNewUser(passwordEncoder));
        AppLog.logger(log).system().does("criar usuário").on("userId=" + user.getId()).info();
        return user;
    }
}
