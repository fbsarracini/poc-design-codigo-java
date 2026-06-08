package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.project.Project;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    @Column(name = "visible_to_client", nullable = false)
    private boolean visibleToClient;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @Deprecated
    public Todo() {}

    public Todo(String title, Project project, User assignee, LocalDate dueDate, boolean visibleToClient) {
        Assert.hasText(title, "o título é obrigatório");
        Assert.notNull(project, "o projeto é obrigatório");
        this.title = title;
        this.project = project;
        this.assignee = assignee;
        this.dueDate = dueDate;
        this.visibleToClient = visibleToClient;
        this.completed = false;
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public boolean isCompleted() { return completed; }

    public boolean isVisibleToClient() { return visibleToClient; }

    public Project getProject() { return project; }

    public User getAssignee() { return assignee; }

    public void complete() {
        Assert.isTrue(!completed, "todo já está completo");
        this.completed = true;
        this.completedAt = LocalDateTime.now();
    }
}
