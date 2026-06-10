package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.project.Project;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record NewTodoRequest(
        @NotBlank String title,
        Long assigneeId,
        LocalDate dueDate,
        boolean visibleToClient
) implements @Valid NewTodoData {

    @Override
    public Todo toNewTodo(Project project) {
        return new Todo(title, project, null, dueDate, visibleToClient);
    }

    @Override
    public Todo toNewTodo(Project project, User assignee) {
        return new Todo(title, project, assignee, dueDate, visibleToClient);
    }
}
