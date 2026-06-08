package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.project.ProjectMembershipRepository;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;

@UseCase
public class CompleteTodo {

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
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(NotFoundException::new);

        if (!projectMembershipRepository.existsByProjectAndUser(todo.getProject(), actor)) {
            throw new ForbiddenException();
        }

        boolean isAssignee = actor.equals(todo.getAssignee());
        boolean isAdminOrAbove = membershipRepository
                .findByAccountAndUser(todo.getProject().getAccount(), actor)
                .map(m -> m.isAdminOrAbove())
                .orElse(false);

        if (!isAssignee && !isAdminOrAbove) {
            throw new ForbiddenException();
        }

        todo.complete();
        todoRepository.save(todo);
    }
}
