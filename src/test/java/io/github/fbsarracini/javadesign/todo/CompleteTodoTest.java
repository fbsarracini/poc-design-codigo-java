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
import static io.github.fbsarracini.javadesign.TestFixtures.*;
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
        assignee = user("Assignee", "assignee@test.com");
        adminUser = user("Admin", "admin@test.com");
        memberUser = user("Member", "member@test.com");
        account = account("Conta Teste");
        project = project("Projeto Teste", account);
        todo = todo("Tarefa", project, assignee);
    }

    @Test
    void shouldCompleteWhenActorIsAssignee() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(projectMembershipRepository.existsByProjectAndUser(project, assignee)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, assignee))
                .thenReturn(Optional.of(membershipAs(account, assignee, Role.MEMBER)));

        completeTodo.execute(1L, assignee);

        assertTrue(todo.isCompleted());
        verify(todoRepository).save(todo);
    }

    @Test
    void shouldCompleteWhenActorIsAdmin() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(projectMembershipRepository.existsByProjectAndUser(project, adminUser)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, adminUser))
                .thenReturn(Optional.of(membershipAs(account, adminUser, Role.ADMIN)));

        completeTodo.execute(1L, adminUser);

        assertTrue(todo.isCompleted());
    }

    @Test
    void shouldCompleteWhenActorIsAssigneeAndAdmin() {
        User adminAssignee = user("AdminAssignee", "adminassignee@test.com");
        Todo todoAssignedToAdmin = todo("Tarefa", project, adminAssignee);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todoAssignedToAdmin));
        when(projectMembershipRepository.existsByProjectAndUser(project, adminAssignee)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, adminAssignee))
                .thenReturn(Optional.of(membershipAs(account, adminAssignee, Role.ADMIN)));

        completeTodo.execute(1L, adminAssignee);

        assertTrue(todoAssignedToAdmin.isCompleted());
    }

    @Test
    void shouldCompleteWhenActorIsOwner() {
        User ownerUser = user("Owner", "owner@test.com");
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(projectMembershipRepository.existsByProjectAndUser(project, ownerUser)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, ownerUser))
                .thenReturn(Optional.of(membershipAs(account, ownerUser, Role.OWNER)));

        completeTodo.execute(1L, ownerUser);

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
                .thenReturn(Optional.of(membershipAs(account, memberUser, Role.MEMBER)));

        assertThatThrownBy(() -> completeTodo.execute(1L, memberUser))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void shouldThrowForbiddenWhenProjectMemberHasNoAccountMembership() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(projectMembershipRepository.existsByProjectAndUser(project, memberUser)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, memberUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> completeTodo.execute(1L, memberUser))
                .isInstanceOf(ForbiddenException.class);
    }
}
