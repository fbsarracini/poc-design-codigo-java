package io.github.fbsarracini.javadesign.user;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Deprecated
    public User() {}

    public User(String name, String email, String password) {
        Assert.hasText(name, "o nome é obrigatório");
        Assert.hasText(email, "o email é obrigatório");
        Assert.hasText(password, "a senha é obrigatória");
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Long getId() { return id; }
    public String getName() { return name; }

    @Override
    public String getUsername() {
        return email;
    }
    @Override public String getPassword() {
        return password;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
