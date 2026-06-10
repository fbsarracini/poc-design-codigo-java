package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.account.Role;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.user.User;
import io.github.fbsarracini.javadesign.user.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddUserToProjectTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProjectMembershipRepository projectMembershipRepository;

    @InjectMocks private AddUserToProject addUserToProject;

    private User admin;
    private User memberActor;
    private User targetUser;
    private Account account;
    private Project project;

    @BeforeEach
    void setUp() {
        account = account("Conta Teste");
        admin = user("Admin", "admin@test.com");
        memberActor = user("Member", "member@test.com");
        targetUser = user("Target", "target@test.com");
        project = project("Projeto", account);
    }

    @Test
    void shouldAddUserToProjectSuccessfully() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(membershipAs(account, admin, Role.ADMIN)));
        when(userRepository.findByEmail("target@test.com")).thenReturn(Optional.of(targetUser));
        when(membershipRepository.findByAccountAndUser(account, targetUser))
                .thenReturn(Optional.of(membershipAs(account, targetUser, Role.MEMBER)));
        when(projectMembershipRepository.existsByProjectAndUser(project, targetUser)).thenReturn(false);

        addUserToProject.execute(1L, admin, "target@test.com");

        ArgumentCaptor<ProjectMembership> pmCaptor = ArgumentCaptor.forClass(ProjectMembership.class);
        verify(projectMembershipRepository).save(pmCaptor.capture());
        assertThat(pmCaptor.getValue())
                .hasFieldOrPropertyWithValue("project", project)
                .hasFieldOrPropertyWithValue("user", targetUser);
    }

    @Test
    void shouldBeIdempotentWhenUserAlreadyInProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(membershipAs(account, admin, Role.ADMIN)));
        when(userRepository.findByEmail("target@test.com")).thenReturn(Optional.of(targetUser));
        when(membershipRepository.findByAccountAndUser(account, targetUser))
                .thenReturn(Optional.of(membershipAs(account, targetUser, Role.MEMBER)));
        when(projectMembershipRepository.existsByProjectAndUser(project, targetUser)).thenReturn(true);

        addUserToProject.execute(1L, admin, "target@test.com");

        verify(projectMembershipRepository, never()).save(any(ProjectMembership.class));
    }

    @Test
    void shouldAddUserWhenActorIsOwner() {
        User owner = user("Owner", "owner@test.com");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(membershipRepository.findByAccountAndUser(account, owner))
                .thenReturn(Optional.of(membershipAs(account, owner, Role.OWNER)));
        when(userRepository.findByEmail("target@test.com")).thenReturn(Optional.of(targetUser));
        when(membershipRepository.findByAccountAndUser(account, targetUser))
                .thenReturn(Optional.of(membershipAs(account, targetUser, Role.MEMBER)));
        when(projectMembershipRepository.existsByProjectAndUser(project, targetUser)).thenReturn(false);

        addUserToProject.execute(1L, owner, "target@test.com");

        ArgumentCaptor<ProjectMembership> pmCaptor = ArgumentCaptor.forClass(ProjectMembership.class);
        verify(projectMembershipRepository).save(pmCaptor.capture());
        assertThat(pmCaptor.getValue())
                .hasFieldOrPropertyWithValue("project", project)
                .hasFieldOrPropertyWithValue("user", targetUser);
    }

    @Test
    void shouldThrowWhenProjectNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addUserToProject.execute(99L, admin, "target@test.com"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowForbiddenWhenActorHasNoMembership() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(membershipRepository.findByAccountAndUser(account, admin)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addUserToProject.execute(1L, admin, "target@test.com"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void shouldThrowForbiddenWhenActorIsMember() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(membershipRepository.findByAccountAndUser(account, memberActor))
                .thenReturn(Optional.of(membershipAs(account, memberActor, Role.MEMBER)));

        assertThatThrownBy(() -> addUserToProject.execute(1L, memberActor, "target@test.com"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void shouldThrowWhenTargetUserNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(membershipAs(account, admin, Role.ADMIN)));
        when(userRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addUserToProject.execute(1L, admin, "notfound@test.com"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowWhenTargetIsNotAccountMember() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(membershipAs(account, admin, Role.ADMIN)));
        when(userRepository.findByEmail("target@test.com")).thenReturn(Optional.of(targetUser));
        when(membershipRepository.findByAccountAndUser(account, targetUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addUserToProject.execute(1L, admin, "target@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("membro");
    }
}
