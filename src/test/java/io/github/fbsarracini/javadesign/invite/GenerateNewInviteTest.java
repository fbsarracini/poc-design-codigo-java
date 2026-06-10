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
import static io.github.fbsarracini.javadesign.TestFixtures.*;
import org.junit.jupiter.api.DisplayName;
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

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private InviteRepository inviteRepository;

    @InjectMocks
    private GenerateNewInvite generateNewInvite;

    private User admin;
    private User owner;
    private User member;
    private User client;
    private Account account;
    private NewInviteData newInviteData;

    @BeforeEach
    void setUp() {
        admin = user("Admin", "admin@test.com");
        owner = user("Owner", "owner@test.com");
        member = user("Member", "member@test.com");
        client = user("Client", "client@test.com");
        account = account("Conta Teste");
        newInviteData = new NewInviteRequest("novo@test.com", 7, Role.MEMBER);
    }

    @Test
    @DisplayName("deve gerar convite quando ator é admin e não há convite pendente")
    void shouldGenerateInviteWhenAdminAndNoPendingInvite() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(membershipAs(account, admin, Role.ADMIN)));
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
    @DisplayName("deve gerar convite quando ator é owner e não há convite pendente")
    void shouldGenerateInviteWhenOwnerAndNoPendingInvite() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, owner))
                .thenReturn(Optional.of(membershipAs(account, owner, Role.OWNER)));
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
    @DisplayName("deve lançar exceção quando conta não encontrada")
    void shouldThrowWhenAccountNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generateNewInvite.execute(99L, admin, newInviteData))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("deve lançar Forbidden quando member tenta convidar")
    void shouldThrowForbiddenWhenMemberCannotInvite() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, member))
                .thenReturn(Optional.of(membershipAs(account, member, Role.MEMBER)));

        assertThatThrownBy(() -> generateNewInvite.execute(1L, member, newInviteData))
                .isInstanceOf(ForbiddenException.class);

        verifyNoInteractions(inviteRepository);
    }

    @Test
    @DisplayName("deve lançar Forbidden quando client tenta convidar")
    void shouldThrowForbiddenWhenClientCannotInvite() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, client))
                .thenReturn(Optional.of(membershipAs(account, client, Role.CLIENT)));

        assertThatThrownBy(() -> generateNewInvite.execute(1L, client, newInviteData))
                .isInstanceOf(ForbiddenException.class);

        verifyNoInteractions(inviteRepository);
    }

    @Test
    @DisplayName("deve lançar Forbidden quando usuário não tem membership na conta")
    void shouldThrowForbiddenWhenUserHasNoMembership() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> generateNewInvite.execute(1L, admin, newInviteData))
                .isInstanceOf(ForbiddenException.class);

        verifyNoInteractions(inviteRepository);
    }

    @Test
    @DisplayName("deve lançar exceção quando já existe convite pendente para o email")
    void shouldThrowWhenPendingInviteAlreadyExists() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(membershipAs(account, admin, Role.ADMIN)));
        when(inviteRepository.existsByAccountAndEmailAndStatusAndExpirationDateGreaterThanEqual(
                eq(account), eq("novo@test.com"), eq(InviteStatus.PENDING), eq(LocalDate.now())))
                .thenReturn(true);

        assertThatThrownBy(() -> generateNewInvite.execute(1L, admin, newInviteData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("já existe um convite pendente para este email");

        verify(inviteRepository, never()).save(any(Invite.class));
    }

    @Test
    @DisplayName("deve gerar convite com validade mínima de 1 dia")
    void shouldGenerateInviteWithMinimumOneDayExpiration() {
        NewInviteData oneDayData = new NewInviteRequest("novo@test.com", 1, Role.MEMBER);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(membershipAs(account, admin, Role.ADMIN)));
        when(inviteRepository.existsByAccountAndEmailAndStatusAndExpirationDateGreaterThanEqual(
                eq(account), eq("novo@test.com"), eq(InviteStatus.PENDING), eq(LocalDate.now())))
                .thenReturn(false);

        generateNewInvite.execute(1L, admin, oneDayData);

        ArgumentCaptor<Invite> captor = ArgumentCaptor.forClass(Invite.class);
        verify(inviteRepository).save(captor.capture());
        assertThat(captor.getValue().getExpirationDate()).isEqualTo(LocalDate.now().plusDays(1));
    }

    @Test
    @DisplayName("deve lançar exceção ao convidar com role OWNER")
    void shouldThrowWhenInvitingWithOwnerRole() {
        NewInviteData ownerData = new NewInviteRequest("novo@test.com", 7, Role.OWNER);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(membershipAs(account, admin, Role.ADMIN)));
        when(inviteRepository.existsByAccountAndEmailAndStatusAndExpirationDateGreaterThanEqual(
                eq(account), eq("novo@test.com"), eq(InviteStatus.PENDING), eq(LocalDate.now())))
                .thenReturn(false);

        assertThatThrownBy(() -> generateNewInvite.execute(1L, admin, ownerData))
                .isInstanceOf(IllegalArgumentException.class);

        verify(inviteRepository, never()).save(any(Invite.class));
    }
}
