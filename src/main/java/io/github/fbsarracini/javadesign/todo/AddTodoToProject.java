package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.log.AppLog;
import io.github.fbsarracini.javadesign.project.Project;
import io.github.fbsarracini.javadesign.project.ProjectMembershipRepository;
import io.github.fbsarracini.javadesign.project.ProjectRepository;
import io.github.fbsarracini.javadesign.user.User;
import io.github.fbsarracini.javadesign.user.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@UseCase
public class AddTodoToProject {

    private static final Logger log = LoggerFactory.getLogger(AddTodoToProject.class);

    private final ProjectRepository projectRepository;
    private final ProjectMembershipRepository projectMembershipRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    public AddTodoToProject(ProjectRepository projectRepository,
                            ProjectMembershipRepository projectMembershipRepository,
                            UserRepository userRepository,
                            TodoRepository todoRepository) {
        this.projectRepository = projectRepository;
        this.projectMembershipRepository = projectMembershipRepository;
        this.userRepository = userRepository;
        this.todoRepository = todoRepository;
    }

    @Transactional
    public Todo execute(@Positive Long projectId, User loggedUser, @Valid NewTodoData newTodoData) {
        AppLog.logger(log).who(loggedUser).does("adicionar todo").on("project=" + projectId).debug();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NotFoundException::new);

        if (!projectMembershipRepository.existsByProjectAndUser(project, loggedUser)) {
            AppLog.logger(log).who(loggedUser).does("adicionar todo").on("project=" + projectId).failed("sem membership no projeto").warn();
            throw new ForbiddenException();
        }

        Optional<User> assignee = Optional.ofNullable(newTodoData.assigneeId())
                .map(id -> {
                    AppLog.logger(log).who(loggedUser).does("adicionar todo").on("assigneeId=" + id).debug();
                    return userRepository.findById(id).orElseThrow(NotFoundException::new);
                });

        AppLog.logger(log).who(loggedUser).does("adicionar todo").on("project=" + projectId).info();
        Todo todo = todoRepository.save(
                assignee.map(user -> newTodoData.toNewTodo(project, user))
                        .orElseGet(() -> newTodoData.toNewTodo(project)));
        AppLog.logger(log).who(loggedUser).does("adicionar todo").on("project=" + projectId + " todo=" + todo.getId()).info();
        return todo;
    }
}
