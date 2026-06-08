package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.account.Account;
import jakarta.persistence.*;
import org.springframework.util.Assert;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Deprecated
    public Project() {}

    public Project(String name, Account account) {
        Assert.hasText(name, "o nome é obrigatório");
        Assert.notNull(account, "a conta é obrigatória");
        this.name = name;
        this.account = account;
        this.status = ProjectStatus.ACTIVE;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public ProjectStatus getStatus() { return status; }
    public Account getAccount() { return account; }
}
