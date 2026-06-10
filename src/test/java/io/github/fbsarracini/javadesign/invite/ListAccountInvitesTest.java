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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAccountInvitesTest {

    @Mock private AccountRepository accountRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private InviteRepository inviteRepository;

    @InjectMocks private ListAccountInvites listAccountInvites;

    private User admin;
    private User member;
    private Account account;

    @BeforeEach
    void setUp() {
        account = account("Conta Teste");
        admin = user("Admin", "admin@test.com");
        member = user("Member", "member@test.com");
    }

    @Test
    void shouldReturnPendingInvitesForAdmin() {
        Invite invite = pendingInvite("novo@test.com", account, Role.MEMBER);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(membershipAs(account, admin, Role.ADMIN)));
        when(inviteRepository.findByAccountAndStatusAndExpirationDateGreaterThanEqual(
                eq(account), eq(InviteStatus.PENDING), eq(LocalDate.now())))
                .thenReturn(List.of(invite));

        List<InviteSummaryResponse> result = listAccountInvites.execute(1L, admin);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).email()).isEqualTo("novo@test.com");
    }

    @Test
    void shouldReturnPendingInvitesForOwner() {
        User owner = user("Owner", "owner@test.com");
        Invite invite = pendingInvite("novo@test.com", account, Role.MEMBER);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, owner))
                .thenReturn(Optional.of(membershipAs(account, owner, Role.OWNER)));
        when(inviteRepository.findByAccountAndStatusAndExpirationDateGreaterThanEqual(
                eq(account), eq(InviteStatus.PENDING), eq(LocalDate.now())))
                .thenReturn(List.of(invite));

        List<InviteSummaryResponse> result = listAccountInvites.execute(1L, owner);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listAccountInvites.execute(99L, admin))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowForbiddenWhenNoMembership() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, admin)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listAccountInvites.execute(1L, admin))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void shouldThrowForbiddenWhenMemberCannotListInvites() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, member))
                .thenReturn(Optional.of(membershipAs(account, member, Role.MEMBER)));

        assertThatThrownBy(() -> listAccountInvites.execute(1L, member))
                .isInstanceOf(ForbiddenException.class);
    }
}
