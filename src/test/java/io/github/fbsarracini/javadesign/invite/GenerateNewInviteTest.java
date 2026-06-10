package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.AccountRepository;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.account.Role;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateNewInviteTest {

    @Mock private AccountRepository accountRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private InviteRepository inviteRepository;

    @InjectMocks private GenerateNewInvite generateNewInvite;

    private User admin;
    private User owner;
    private User member;
    private User client;
    private Account account;
    private NewInviteData newInviteData;

    @BeforeEach
    void setUp() {
        admin = new User("Admin", "admin@test.com", "hash");
        owner = new User("Owner", "owner@test.com", "hash");
        member = new User("Member", "member@test.com", "hash");
        client = new User("Client", "client@test.com", "hash");
        account = new Account("Conta Teste");
        newInviteData = new NewInviteRequest("novo@test.com", 7, Role.MEMBER);
    }

    @Test
    void shouldGenerateInviteWhenAdminAndNoPendingInvite() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(new Membership(account, admin, Role.ADMIN)));
        when(inviteRepository.existsByAccountAndEmailAndStatusAndExpirationDateGreaterThanEqual(
                eq(account), eq("novo@test.com"), eq(InviteStatus.PENDING), eq(LocalDate.now())))
                .thenReturn(false);

        generateNewInvite.execute(1L, admin, newInviteData);

        ArgumentCaptor<Invite> captor = ArgumentCaptor.forClass(Invite.class);
        verify(inviteRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("novo@test.com");
        assertThat(captor.getValue().getRole()).isEqualTo(Role.MEMBER);
    }

    @Test
    void shouldGenerateInviteWhenOwnerAndNoPendingInvite() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, owner))
                .thenReturn(Optional.of(new Membership(account, owner, Role.OWNER)));
        when(inviteRepository.existsByAccountAndEmailAndStatusAndExpirationDateGreaterThanEqual(
                eq(account), eq("novo@test.com"), eq(InviteStatus.PENDING), eq(LocalDate.now())))
                .thenReturn(false);

        generateNewInvite.execute(1L, owner, newInviteData);

        ArgumentCaptor<Invite> captor = ArgumentCaptor.forClass(Invite.class);
        verify(inviteRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("novo@test.com");
        assertThat(captor.getValue().getRole()).isEqualTo(Role.MEMBER);
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generateNewInvite.execute(99L, admin, newInviteData))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowForbiddenWhenMemberCannotInvite() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, member))
                .thenReturn(Optional.of(new Membership(account, member, Role.MEMBER)));

        assertThatThrownBy(() -> generateNewInvite.execute(1L, member, newInviteData))
                .isInstanceOf(ForbiddenException.class);

        verifyNoInteractions(inviteRepository);
    }

    @Test
    void shouldThrowForbiddenWhenClientCannotInvite() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, client))
                .thenReturn(Optional.of(new Membership(account, client, Role.CLIENT)));

        assertThatThrownBy(() -> generateNewInvite.execute(1L, client, newInviteData))
                .isInstanceOf(ForbiddenException.class);

        verifyNoInteractions(inviteRepository);
    }

    @Test
    void shouldThrowForbiddenWhenUserHasNoMembership() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> generateNewInvite.execute(1L, admin, newInviteData))
                .isInstanceOf(ForbiddenException.class);

        verifyNoInteractions(inviteRepository);
    }

    @Test
    void shouldThrowWhenPendingInviteAlreadyExists() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(new Membership(account, admin, Role.ADMIN)));
        when(inviteRepository.existsByAccountAndEmailAndStatusAndExpirationDateGreaterThanEqual(
                eq(account), eq("novo@test.com"), eq(InviteStatus.PENDING), eq(LocalDate.now())))
                .thenReturn(true);

        assertThatThrownBy(() -> generateNewInvite.execute(1L, admin, newInviteData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("já existe um convite pendente para este email");

        verify(inviteRepository, never()).save(any(Invite.class));
    }
}
