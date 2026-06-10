package io.github.fbsarracini.javadesign.user;

import io.github.fbsarracini.javadesign.exception.UnprocessableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateNewUserTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private NewUserData newUserData;

    @InjectMocks private CreateNewUser createNewUser;

    @Test
    void shouldCreateUserWhenEmailNotRegistered() {
        User expected = new User("João", "joao@test.com", "encoded");
        when(newUserData.getEmail()).thenReturn("joao@test.com");
        when(userRepository.findByEmail("joao@test.com")).thenReturn(Optional.empty());
        when(newUserData.toNewUser(passwordEncoder)).thenReturn(expected);
        when(userRepository.save(expected)).thenReturn(expected);

        User result = createNewUser.execute(newUserData);

        assertEquals(expected, result);
        verify(userRepository).save(expected);
    }

    @Test
    void shouldThrowWhenEmailAlreadyRegistered() {
        User existing = new User("Existente", "joao@test.com", "hash");
        when(newUserData.getEmail()).thenReturn("joao@test.com");
        when(userRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> createNewUser.execute(newUserData))
                .isInstanceOf(UnprocessableException.class);
    }
}
