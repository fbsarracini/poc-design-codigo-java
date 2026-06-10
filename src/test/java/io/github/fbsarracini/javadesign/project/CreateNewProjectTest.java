package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.AccountRepository;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.account.Role;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.user.User;
import org.junit.jupiter.api.BeforeEach;
import static io.github.fbsarracini.javadesign.TestFixtures.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateNewProjectTest {

    @Mock private AccountRepository accountRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectMembershipRepository projectMembershipRepository;

    @InjectMocks private CreateNewProject createNewProject;

    private User member;
    private User client;
    private Account account;
    private NewProjectData request;

    @BeforeEach
    void setUp() {
        member = user("Member", "member@test.com");
        client = user("Client", "client@test.com");
        account = account("Conta Teste");
        request = new NewProjectRequest("Projeto Novo");
    }

    @Test
    void shouldCreateProjectWhenMemberCanCreate() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, member))
                .thenReturn(Optional.of(membershipAs(account, member, Role.MEMBER)));
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        when(projectRepository.save(projectCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        Project result = createNewProject.execute(1L, member, request);

        assertThat(result.getName()).isEqualTo("Projeto Novo");
        assertThat(result.getAccount()).isEqualTo(account);

        ArgumentCaptor<ProjectMembership> captor = ArgumentCaptor.forClass(ProjectMembership.class);
        verify(projectMembershipRepository).save(captor.capture());
    }

    @Test
    void shouldCreateProjectWhenAdminCanCreate() {
        User admin = user("Admin", "admin@test.com");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(membershipAs(account, admin, Role.ADMIN)));
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        when(projectRepository.save(projectCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        Project result = createNewProject.execute(1L, admin, request);

        assertThat(result.getName()).isEqualTo("Projeto Novo");
    }

    @Test
    void shouldCreateProjectWhenOwnerCanCreate() {
        User owner = user("Owner", "owner@test.com");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, owner))
                .thenReturn(Optional.of(membershipAs(account, owner, Role.OWNER)));
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        when(projectRepository.save(projectCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        Project result = createNewProject.execute(1L, owner, request);

        assertThat(result.getName()).isEqualTo("Projeto Novo");
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createNewProject.execute(99L, member, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowForbiddenWhenClientCannotCreateProject() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, client))
                .thenReturn(Optional.of(membershipAs(account, client, Role.CLIENT)));

        assertThatThrownBy(() -> createNewProject.execute(1L, client, request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void shouldThrowForbiddenWhenNoMembership() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, member)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createNewProject.execute(1L, member, request))
                .isInstanceOf(ForbiddenException.class);
    }
}
