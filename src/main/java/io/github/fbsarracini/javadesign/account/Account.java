package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.persistence.*;
import org.springframework.util.Assert;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Deprecated
    public Account() {
    }

    public Account(String name, User owner) {
        Assert.hasText(name, "o nome é obrigatório");
        Assert.notNull(owner, "usuário owner não pode estar nulo");
        this.name = name;
        this.owner = owner;
    }

    public Long getId() { return id; }

    public boolean belongsTo(User loggedUser) {
        Assert.isTrue(loggedUser != null, "O usuário logado não pode ser nulo");
        return this.owner.equals(loggedUser);
    }
}
