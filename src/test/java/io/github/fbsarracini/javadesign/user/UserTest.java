package io.github.fbsarracini.javadesign.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UserTest {

    @Test
    @DisplayName("deve criar usuário com dados válidos")
    void shouldCreateUserWithValidData() {
        User user = new User("João", "joao@test.com", "senha123");

        assertEquals("João", user.getName());
        assertEquals("joao@test.com", user.getUsername());
    }

    @Test
    @DisplayName("deve lançar exceção quando nome é vazio")
    void shouldThrowWhenNameIsBlank() {
        assertThatThrownBy(() -> new User("", "joao@test.com", "senha123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nome");
    }

    @Test
    @DisplayName("deve lançar exceção quando email é vazio")
    void shouldThrowWhenEmailIsBlank() {
        assertThatThrownBy(() -> new User("João", "", "senha123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    @DisplayName("deve lançar exceção quando senha é vazia")
    void shouldThrowWhenPasswordIsBlank() {
        assertThatThrownBy(() -> new User("João", "joao@test.com", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("senha");
    }

    @Test
    @DisplayName("deve lançar exceção quando nome é nulo")
    void shouldThrowWhenNameIsNull() {
        assertThatThrownBy(() -> new User(null, "joao@test.com", "senha123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nome");
    }

    @Test
    @DisplayName("deve lançar exceção quando email é nulo")
    void shouldThrowWhenEmailIsNull() {
        assertThatThrownBy(() -> new User("João", null, "senha123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    @DisplayName("deve lançar exceção quando senha é nula")
    void shouldThrowWhenPasswordIsNull() {
        assertThatThrownBy(() -> new User("João", "joao@test.com", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("senha");
    }

    @Test
    @DisplayName("deve lançar exceção quando nome é espaço em branco")
    void shouldThrowWhenNameIsWhitespace() {
        assertThatThrownBy(() -> new User("   ", "joao@test.com", "senha123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nome");
    }

    @Test
    @DisplayName("deve considerar usuários iguais pelo email")
    void shouldConsiderUsersEqualByEmail() {
        User a = new User("João", "joao@test.com", "hash1");
        User b = new User("João Silva", "joao@test.com", "hash2");

        assertEquals(a, b);
    }

    @Test
    @DisplayName("deve retornar false ao comparar com null")
    void shouldReturnFalseWhenComparingWithNull() {
        User user = new User("João", "joao@test.com", "hash");

        assertFalse(user.equals(null));
    }

    @Test
    @DisplayName("deve retornar false ao comparar com objeto de classe diferente")
    void shouldReturnFalseWhenComparingWithDifferentClass() {
        User user = new User("João", "joao@test.com", "hash");

        assertFalse(user.equals("joao@test.com"));
    }
}
