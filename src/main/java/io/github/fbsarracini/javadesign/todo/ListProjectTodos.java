package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.account.Role;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.log.AppLog;
import io.github.fbsarracini.javadesign.project.Project;
import io.github.fbsarracini.javadesign.project.ProjectMembershipRepository;
import io.github.fbsarracini.javadesign.project.ProjectRepository;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@UseCase
public class ListProjectTodos {

    private static final Logger log = LoggerFactory.getLogger(ListProjectTodos.class);

    private final ProjectRepository projectRepository;
    private final ProjectMembershipRepository projectMembershipRepository;
    private final MembershipRepository membershipRepository;
    private final TodoRepository todoRepository;

    public ListProjectTodos(ProjectRepository projectRepository,
                            ProjectMembershipRepository projectMembershipRepository,
                            MembershipRepository membershipRepository,
                            TodoRepository todoRepository) {
        this.projectRepository = projectRepository;
        this.projectMembershipRepository = projectMembershipRepository;
        this.membershipRepository = membershipRepository;
        this.todoRepository = todoRepository;
    }

    public Page<Todo> execute(@Positive Long projectId, User loggedUser, Pageable pageable) {
        AppLog.logger(log).who(loggedUser).does("listar todos").on("project=" + projectId).debug();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NotFoundException::new);

        if (!projectMembershipRepository.existsByProjectAndUser(project, loggedUser)) {
            AppLog.logger(log).who(loggedUser).does("listar todos").on("project=" + projectId).failed("sem membership no projeto").warn();
            throw new ForbiddenException();
        }

        Role role = membershipRepository.findByAccountAndUser(project.getAccount(), loggedUser)
                .map(Membership::getRole)
                .orElseThrow(() -> {
                    AppLog.logger(log).who(loggedUser).does("listar todos").on("project=" + projectId).failed("sem membership na conta").warn();
                    return new ForbiddenException();
                });

        if (role == Role.CLIENT) {
            AppLog.logger(log).who(loggedUser).does("listar todos").on("project=" + projectId + " filtro=visibleToClient").debug();
            return todoRepository.findByProjectAndVisibleToClient(project, true, pageable);
        }

        AppLog.logger(log).who(loggedUser).does("listar todos").on("project=" + projectId).debug();
        return todoRepository.findByProject(project, pageable);
    }
}
