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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateNewInviteTest {

    @Mock private AccountRepository accountRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private InviteRepository inviteRepository;
    @Mock private NewInviteData newInviteData;

    @InjectMocks private GenerateNewInvite generateNewInvite;

    private User admin;
    private User member;
    private Account account;

    @BeforeEach
    void setUp() {
        admin = new User("Admin", "admin@test.com", "hash");
        member = new User("Member", "member@test.com", "hash");
        account = new Account("Conta Teste");
    }

    @Test
    void shouldGenerateInviteWhenAdminAndNoPendingInvite() {
        when(newInviteData.email()).thenReturn("novo@test.com");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(new Membership(account, admin, Role.ADMIN)));
        when(inviteRepository.existsByAccountAndEmailAndStatusAndExpirationDateGreaterThanEqual(
                any(), any(), any(), any())).thenReturn(false);

        generateNewInvite.execute(1L, admin, newInviteData);

        verify(inviteRepository).save(any());
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

        verify(inviteRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPendingInviteAlreadyExists() {
        when(newInviteData.email()).thenReturn("novo@test.com");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(new Membership(account, admin, Role.ADMIN)));
        when(inviteRepository.existsByAccountAndEmailAndStatusAndExpirationDateGreaterThanEqual(
                any(), any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> generateNewInvite.execute(1L, admin, newInviteData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("já existe um convite pendente para este email");

        verify(inviteRepository, never()).save(any());
    }
}
