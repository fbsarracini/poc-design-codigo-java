package io.github.fbsarracini.javadesign.auth;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.exception.UnauthorizedException;
import io.github.fbsarracini.javadesign.log.AppLog;
import io.github.fbsarracini.javadesign.user.User;
import io.github.fbsarracini.javadesign.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

@UseCase
public class AuthenticateUser {

    private static final Logger log = LoggerFactory.getLogger(AuthenticateUser.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticateUser(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String execute(String email, String rawPassword) {
        AppLog.logger(log).who(email).does("autenticar").debug();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    AppLog.logger(log).who(email).does("autenticar").failed("usuário não encontrado").warn();
                    return new UnauthorizedException();
                });

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            AppLog.logger(log).who(email).does("autenticar").failed("senha incorreta").warn();
            throw new UnauthorizedException();
        }

        AppLog.logger(log).who(email).does("autenticar").info();
        return jwtService.generateToken(user);
    }
}
