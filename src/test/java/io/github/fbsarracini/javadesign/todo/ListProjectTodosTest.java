package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.account.Role;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.project.Project;
import io.github.fbsarracini.javadesign.project.ProjectMembershipRepository;
import io.github.fbsarracini.javadesign.project.ProjectRepository;
import io.github.fbsarracini.javadesign.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListProjectTodosTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectMembershipRepository projectMembershipRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private TodoRepository todoRepository;

    @InjectMocks private ListProjectTodos listProjectTodos;

    private User memberUser;
    private User clientUser;
    private Account account;
    private Project project;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        account = new Account("Conta Teste");
        memberUser = new User("Member", "member@test.com", "hash");
        clientUser = new User("Client", "client@test.com", "hash");
        project = new Project("Projeto", account);
        pageable = Pageable.unpaged();
    }

    @Test
    void shouldReturnAllTodosForMember() {
        Page<Todo> page = mock(Page.class);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMembershipRepository.existsByProjectAndUser(project, memberUser)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, memberUser))
                .thenReturn(Optional.of(new Membership(account, memberUser, Role.MEMBER)));
        when(todoRepository.findByProject(project, pageable)).thenReturn(page);

        Page<Todo> result = listProjectTodos.execute(1L, memberUser, pageable);

        assertThat(result).isSameAs(page);
        verify(todoRepository).findByProject(project, pageable);
    }

    @Test
    void shouldReturnOnlyVisibleTodosForClient() {
        Page<Todo> page = mock(Page.class);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMembershipRepository.existsByProjectAndUser(project, clientUser)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, clientUser))
                .thenReturn(Optional.of(new Membership(account, clientUser, Role.CLIENT)));
        when(todoRepository.findByProjectAndVisibleToClient(project, true, pageable)).thenReturn(page);

        Page<Todo> result = listProjectTodos.execute(1L, clientUser, pageable);

        assertThat(result).isSameAs(page);
        verify(todoRepository).findByProjectAndVisibleToClient(project, true, pageable);
    }

    @Test
    void shouldThrowWhenProjectNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listProjectTodos.execute(99L, memberUser, pageable))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowForbiddenWhenNotProjectMember() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMembershipRepository.existsByProjectAndUser(project, memberUser)).thenReturn(false);

        assertThatThrownBy(() -> listProjectTodos.execute(1L, memberUser, pageable))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void shouldThrowForbiddenWhenNoAccountMembership() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMembershipRepository.existsByProjectAndUser(project, memberUser)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, memberUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listProjectTodos.execute(1L, memberUser, pageable))
                .isInstanceOf(ForbiddenException.class);
    }
}
