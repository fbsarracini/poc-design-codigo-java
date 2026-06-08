package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.project.Project;
import io.github.fbsarracini.javadesign.project.ProjectMembershipRepository;
import io.github.fbsarracini.javadesign.project.ProjectRepository;
import io.github.fbsarracini.javadesign.user.User;
import io.github.fbsarracini.javadesign.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@UseCase
public class AddTodoToProject {

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
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NotFoundException::new);

        if (!projectMembershipRepository.existsByProjectAndUser(project, loggedUser)) {
            throw new ForbiddenException();
        }

        User assignee = null;
        if (newTodoData.assigneeId() != null) {
            assignee = userRepository.findById(newTodoData.assigneeId())
                    .orElseThrow(NotFoundException::new);
        }

        return todoRepository.save(newTodoData.toNewTodo(project, assignee));
    }
}
