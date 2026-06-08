package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.persistence.*;
import org.springframework.util.Assert;

@Entity
@Table(name = "project_memberships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "user_id"}))
public class ProjectMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Deprecated
    public ProjectMembership() {}

    public ProjectMembership(Project project, User user) {
        Assert.notNull(project, "projeto é obrigatório");
        Assert.notNull(user, "usuário é obrigatório");
        this.project = project;
        this.user = user;
    }
}
