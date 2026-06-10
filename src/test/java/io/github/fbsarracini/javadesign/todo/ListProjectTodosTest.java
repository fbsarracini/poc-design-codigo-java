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
import static io.github.fbsarracini.javadesign.TestFixtures.*;
import org.junit.jupiter.api.DisplayName;
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

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMembershipRepository projectMembershipRepository;
    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private ListProjectTodos listProjectTodos;

    private User memberUser;
    private User clientUser;
    private Account account;
    private Project project;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        account = account("Conta Teste");
        memberUser = user("Member", "member@test.com");
        clientUser = user("Client", "client@test.com");
        project = project("Projeto", account);
        pageable = Pageable.unpaged();
    }

    @Test
    @DisplayName("deve retornar todas as tarefas para member")
    void shouldReturnAllTodosForMember() {
        Page<Todo> page = mock(Page.class);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMembershipRepository.existsByProjectAndUser(project, memberUser)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, memberUser))
                .thenReturn(Optional.of(membershipAs(account, memberUser, Role.MEMBER)));
        when(todoRepository.findByProject(project, pageable)).thenReturn(page);

        Page<Todo> result = listProjectTodos.execute(1L, memberUser, pageable);

        assertThat(result).isSameAs(page);
        verify(todoRepository).findByProject(project, pageable);
    }

    @Test
    @DisplayName("deve retornar apenas tarefas visíveis para client")
    void shouldReturnOnlyVisibleTodosForClient() {
        Page<Todo> page = mock(Page.class);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMembershipRepository.existsByProjectAndUser(project, clientUser)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, clientUser))
                .thenReturn(Optional.of(membershipAs(account, clientUser, Role.CLIENT)));
        when(todoRepository.findByProjectAndVisibleToClient(project, true, pageable)).thenReturn(page);

        Page<Todo> result = listProjectTodos.execute(1L, clientUser, pageable);

        assertThat(result).isSameAs(page);
        verify(todoRepository).findByProjectAndVisibleToClient(project, true, pageable);
    }

    @Test
    @DisplayName("deve lançar exceção quando projeto não encontrado")
    void shouldThrowWhenProjectNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listProjectTodos.execute(99L, memberUser, pageable))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("deve lançar Forbidden quando não é membro do projeto")
    void shouldThrowForbiddenWhenNotProjectMember() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMembershipRepository.existsByProjectAndUser(project, memberUser)).thenReturn(false);

        assertThatThrownBy(() -> listProjectTodos.execute(1L, memberUser, pageable))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("deve lançar Forbidden quando não tem membership na conta")
    void shouldThrowForbiddenWhenNoAccountMembership() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMembershipRepository.existsByProjectAndUser(project, memberUser)).thenReturn(true);
        when(membershipRepository.findByAccountAndUser(account, memberUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listProjectTodos.execute(1L, memberUser, pageable))
                .isInstanceOf(ForbiddenException.class);
    }
}
