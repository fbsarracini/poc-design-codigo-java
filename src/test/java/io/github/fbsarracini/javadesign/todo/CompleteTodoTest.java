package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.account.Role;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.project.Project;
import io.github.fbsarracini.javadesign.project.ProjectMembershipRepository;
import io.github.fbsarracini.javadesign.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompleteTodoTest {

    @Mock private TodoRepository todoRepository;
    @Mock private ProjectMembershipRepository projectMembershipRepository;
    @Mock private MembershipRepository membershipRepository;

    @InjectMocks private CompleteTodo completeTodo;

    private User assignee;
    private User adminUser;
    private User memberUser;
    private Account account;
    private Project project;
    private Todo todo;

    @BeforeEach
    void setUp() {
        assignee = new User("Assignee", "assignee@test.com", "hash");
        adminUser = new User("Admin", "admin@test.com", "hash");
        memberUser = new User("Member", "member@test.com", "hash");
        account = new Account("Conta Teste");
        project = new Project("Projeto Teste", account);
        todo = new Todo("Tarefa", project, assignee, null, false);
    }

    @Test
    void shouldCompleteWhenActorIsAssignee() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(projectMembershipRepository.existsByProjectAndUser(project, assignee)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, assignee))
                .thenReturn(Optional.of(new Membership(account, assignee, Role.MEMBER)));

        completeTodo.execute(1L, assignee);

        assertTrue(todo.isCompleted());
        verify(todoRepository).save(todo);
    }

    @Test
    void shouldCompleteWhenActorIsAdmin() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(projectMembershipRepository.existsByProjectAndUser(project, adminUser)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, adminUser))
                .thenReturn(Optional.of(new Membership(account, adminUser, Role.ADMIN)));

        completeTodo.execute(1L, adminUser);

        assertTrue(todo.isCompleted());
    }

    @Test
    void shouldThrowWhenTodoNotFound() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> completeTodo.execute(99L, assignee))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowForbiddenWhenNotProjectMember() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(projectMembershipRepository.existsByProjectAndUser(project, memberUser)).thenReturn(false);

        assertThatThrownBy(() -> completeTodo.execute(1L, memberUser))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void shouldThrowForbiddenWhenMemberIsNeitherAssigneeNorAdmin() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(projectMembershipRepository.existsByProjectAndUser(project, memberUser)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, memberUser))
                .thenReturn(Optional.of(new Membership(account, memberUser, Role.MEMBER)));

        assertThatThrownBy(() -> completeTodo.execute(1L, memberUser))
                .isInstanceOf(ForbiddenException.class);
    }
}
