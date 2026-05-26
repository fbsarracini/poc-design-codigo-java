package io.github.fbsarracini.javadesign.auth;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.exception.UnauthorizedException;
import io.github.fbsarracini.javadesign.user.User;
import io.github.fbsarracini.javadesign.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@UseCase
public class AuthenticateUser {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticateUser(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String execute(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UnauthorizedException::new);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UnauthorizedException();
        }

        return jwtService.generateToken(user);
    }
}
