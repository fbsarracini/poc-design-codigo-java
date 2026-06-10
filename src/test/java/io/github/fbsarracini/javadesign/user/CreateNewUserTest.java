package io.github.fbsarracini.javadesign.user;

import io.github.fbsarracini.javadesign.exception.UnprocessableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static io.github.fbsarracini.javadesign.TestFixtures.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateNewUserTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks private CreateNewUser createNewUser;

    @Test
    void shouldCreateUserWhenEmailNotRegistered() {
        NewUserRequest request = new NewUserRequest("João", "joao@test.com", "senha_longa_123");
        when(userRepository.findByEmail("joao@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha_longa_123")).thenReturn("encoded");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        User result = createNewUser.execute(request);

        assertThat(result.getName()).isEqualTo("João");
        assertThat(result.getUsername()).isEqualTo("joao@test.com");
    }

    @Test
    void shouldThrowWhenEmailAlreadyRegistered() {
        NewUserRequest request = new NewUserRequest("João", "joao@test.com", "senha_longa_123");
        User existing = user("Existente", "joao@test.com");
        when(userRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> createNewUser.execute(request))
                .isInstanceOf(UnprocessableException.class);
    }

    @Test
    void shouldThrowWhenUserNameIsBlank() {
        NewUserRequest request = new NewUserRequest("", "joao@test.com", "senha_longa_123");
        when(userRepository.findByEmail("joao@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha_longa_123")).thenReturn("encoded");

        assertThatThrownBy(() -> createNewUser.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nome");
    }
}
