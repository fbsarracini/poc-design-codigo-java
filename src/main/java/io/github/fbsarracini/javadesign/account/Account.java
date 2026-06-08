package io.github.fbsarracini.javadesign.account;

import jakarta.persistence.*;
import org.springframework.util.Assert;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Deprecated
    public Account() {}

    public Account(String name) {
        Assert.hasText(name, "o nome é obrigatório");
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
}
