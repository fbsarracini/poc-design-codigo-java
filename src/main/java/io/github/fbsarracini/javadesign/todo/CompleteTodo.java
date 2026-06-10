package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.log.AppLog;
import io.github.fbsarracini.javadesign.project.ProjectMembershipRepository;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UseCase
public class CompleteTodo {

    private static final Logger log = LoggerFactory.getLogger(CompleteTodo.class);

    private final TodoRepository todoRepository;
    private final ProjectMembershipRepository projectMembershipRepository;
    private final MembershipRepository membershipRepository;

    public CompleteTodo(TodoRepository todoRepository,
                        ProjectMembershipRepository projectMembershipRepository,
                        MembershipRepository membershipRepository) {
        this.todoRepository = todoRepository;
        this.projectMembershipRepository = projectMembershipRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public void execute(@Positive Long todoId, User actor) {
        AppLog.logger(log).who(actor).does("completar todo").on("todo=" + todoId).debug();
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(NotFoundException::new);

        if (!projectMembershipRepository.existsByProjectAndUser(todo.getProject(), actor)) {
            AppLog.logger(log).who(actor).does("completar todo").on("todo=" + todoId).failed("sem membership no projeto").warn();
            throw new ForbiddenException();
        }

        boolean isAssignee = actor.equals(todo.getAssignee());
        boolean isAdminOrAbove = membershipRepository
                .findByAccountAndUser(todo.getProject().getAccount(), actor)
                .map(m -> m.isAdminOrAbove())
                .orElse(false);

        AppLog.logger(log).who(actor).does("completar todo").on("todo=" + todoId + " isAssignee=" + isAssignee + " isAdminOrAbove=" + isAdminOrAbove).debug();
        if (!isAssignee && !isAdminOrAbove) {
            AppLog.logger(log).who(actor).does("completar todo").on("todo=" + todoId).failed("não é assignee nem admin").warn();
            throw new ForbiddenException();
        }

        todo.complete();
        AppLog.logger(log).who(actor).does("completar todo").on("todo=" + todoId).info();
        todoRepository.save(todo);
        AppLog.logger(log).who(actor).does("completar todo").on("todo=" + todoId).info();
    }
}
