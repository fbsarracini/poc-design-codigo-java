package io.github.fbsarracini.javadesign.auth;

import io.github.fbsarracini.javadesign.exception.UnauthorizedException;
import io.github.fbsarracini.javadesign.user.User;
import io.github.fbsarracini.javadesign.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks private AuthenticateUser authenticateUser;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("João", "joao@test.com", "encoded_password");
    }

    @Test
    void shouldReturnTokenWhenCredentialsValid() {
        when(userRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw", "encoded_password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt_token");

        String token = authenticateUser.execute("joao@test.com", "raw");

        assertEquals("jwt_token", token);
    }

    @Test
    void shouldThrowWhenEmailNotFound() {
        when(userRepository.findByEmail("naoexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticateUser.execute("naoexiste@test.com", "qualquer"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void shouldThrowWhenPasswordIncorrect() {
        when(userRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("errada", "encoded_password")).thenReturn(false);

        assertThatThrownBy(() -> authenticateUser.execute("joao@test.com", "errada"))
                .isInstanceOf(UnauthorizedException.class);
    }
}
