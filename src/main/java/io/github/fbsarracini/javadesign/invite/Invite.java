package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.Role;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InviteStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Deprecated
    public Invite() {}

    public Invite(String email, LocalDate expirationDate, Account account, Role role) {
        Assert.hasText(email, "o email precisa estar preenchido");
        Assert.isTrue(expirationDate.isAfter(LocalDate.now()), "a data de expiração do convite tem que ser no futuro");
        Assert.notNull(account, "a conta nao pode ser nula");
        Assert.notNull(role, "o papel é obrigatório");
        Assert.isTrue(role != Role.OWNER, "não é possível convidar alguém como OWNER");

        this.email = email;
        this.expirationDate = expirationDate;
        this.account = account;
        this.role = role;
        this.token = UUID.randomUUID().toString();
        this.status = InviteStatus.PENDING;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public String getToken() { return token; }
    public InviteStatus getStatus() { return status; }
    public Role getRole() { return role; }
    public Account getAccount() { return account; }

    public void revoke() {
        Assert.isTrue(status == InviteStatus.PENDING, "convite não está pendente");
        this.status = InviteStatus.REVOKED;
    }

    public Membership accept(User user) {
        Assert.isTrue(status == InviteStatus.PENDING, "convite já foi utilizado ou revogado");
        Assert.isTrue(!LocalDate.now().isAfter(expirationDate), "convite expirado");
        Assert.isTrue(this.email.equalsIgnoreCase(user.getUsername()), "email do usuário não corresponde ao convite");

        this.status = InviteStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();

        return new Membership(account, user, role);
    }
}
