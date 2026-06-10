package io.github.fbsarracini.javadesign.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void shouldCreateUserWithValidData() {
        User user = new User("João", "joao@test.com", "senha123");

        assertEquals("João", user.getName());
        assertEquals("joao@test.com", user.getUsername());
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThatThrownBy(() -> new User("", "joao@test.com", "senha123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nome");
    }

    @Test
    void shouldThrowWhenEmailIsBlank() {
        assertThatThrownBy(() -> new User("João", "", "senha123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void shouldThrowWhenPasswordIsBlank() {
        assertThatThrownBy(() -> new User("João", "joao@test.com", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("senha");
    }

    @Test
    void shouldConsiderUsersEqualByEmail() {
        User a = new User("João", "joao@test.com", "hash1");
        User b = new User("João Silva", "joao@test.com", "hash2");

        assertEquals(a, b);
    }
}
