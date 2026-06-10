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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
        account = new Account("Conta Teste");
        admin = new User("Admin", "admin@test.com", "hash");
        memberActor = new User("Member", "member@test.com", "hash");
        targetUser = new User("Target", "target@test.com", "hash");
        project = new Project("Projeto", account);
    }

    @Test
    void shouldAddUserToProjectSuccessfully() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(new Membership(account, admin, Role.ADMIN)));
        when(userRepository.findByEmail("target@test.com")).thenReturn(Optional.of(targetUser));
        when(membershipRepository.findByAccountAndUser(account, targetUser))
                .thenReturn(Optional.of(new Membership(account, targetUser, Role.MEMBER)));
        when(projectMembershipRepository.existsByProjectAndUser(project, targetUser)).thenReturn(false);

        addUserToProject.execute(1L, admin, "target@test.com");

        verify(projectMembershipRepository).save(any(ProjectMembership.class));
    }

    @Test
    void shouldBeIdempotentWhenUserAlreadyInProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(new Membership(account, admin, Role.ADMIN)));
        when(userRepository.findByEmail("target@test.com")).thenReturn(Optional.of(targetUser));
        when(membershipRepository.findByAccountAndUser(account, targetUser))
                .thenReturn(Optional.of(new Membership(account, targetUser, Role.MEMBER)));
        when(projectMembershipRepository.existsByProjectAndUser(project, targetUser)).thenReturn(true);

        addUserToProject.execute(1L, admin, "target@test.com");

        verify(projectMembershipRepository, never()).save(any());
    }

    @Test
    void shouldAddUserWhenActorIsOwner() {
        User owner = new User("Owner", "owner@test.com", "hash");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(membershipRepository.findByAccountAndUser(account, owner))
                .thenReturn(Optional.of(new Membership(account, owner, Role.OWNER)));
        when(userRepository.findByEmail("target@test.com")).thenReturn(Optional.of(targetUser));
        when(membershipRepository.findByAccountAndUser(account, targetUser))
                .thenReturn(Optional.of(new Membership(account, targetUser, Role.MEMBER)));
        when(projectMembershipRepository.existsByProjectAndUser(project, targetUser)).thenReturn(false);

        addUserToProject.execute(1L, owner, "target@test.com");

        verify(projectMembershipRepository).save(any(ProjectMembership.class));
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
                .thenReturn(Optional.of(new Membership(account, memberActor, Role.MEMBER)));

        assertThatThrownBy(() -> addUserToProject.execute(1L, memberActor, "target@test.com"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void shouldThrowWhenTargetUserNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(new Membership(account, admin, Role.ADMIN)));
        when(userRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addUserToProject.execute(1L, admin, "notfound@test.com"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowWhenTargetIsNotAccountMember() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(new Membership(account, admin, Role.ADMIN)));
        when(userRepository.findByEmail("target@test.com")).thenReturn(Optional.of(targetUser));
        when(membershipRepository.findByAccountAndUser(account, targetUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addUserToProject.execute(1L, admin, "target@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("membro");
    }
}
