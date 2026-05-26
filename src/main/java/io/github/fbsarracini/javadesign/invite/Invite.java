package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.account.Account;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDate;

@Entity
@Table(name = "invites")
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    // CDI eyes only
    @Deprecated
    public Invite() {
    }

    public Invite(String email, LocalDate expirationDate, Account account) {

        Assert.hasText(email, "o email precisa estar preenchido");

        Assert.isTrue(expirationDate.isAfter(LocalDate.now()), "a data de expiração do convite tem que ser no futuro");

        Assert.notNull(account, "a conta nao pode ser nula");

        this.email = email;
        this.expirationDate = expirationDate;
        this.account = account;
    }

}
