package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.project.Project;
import io.github.fbsarracini.javadesign.user.User;

public interface NewTodoData {
    Long assigneeId();
    Todo toNewTodo(Project project, User assignee);
}
