package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.persistence.*;
import org.springframework.util.Assert;

@Entity
@Table(name = "memberships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "user_id"}))
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Deprecated
    public Membership() {}

    public Membership(Account account, User user, Role role) {
        Assert.notNull(account, "account é obrigatório");
        Assert.notNull(user, "user é obrigatório");
        Assert.notNull(role, "role é obrigatório");
        this.account = account;
        this.user = user;
        this.role = role;
    }

    public Account getAccount() { return account; }
    public User getUser() { return user; }
    public Role getRole() { return role; }

    public boolean canInvite() {
        return role.canInvite();
    }

    public boolean canCreateProject() {
        return role.canCreateProject();
    }

    public boolean isAdminOrAbove() {
        return role.isAdminOrAbove();
    }
}
