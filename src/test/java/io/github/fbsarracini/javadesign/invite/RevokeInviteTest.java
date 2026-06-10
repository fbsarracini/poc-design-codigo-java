package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.account.Account;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevokeInviteTest {

    @Mock private InviteRepository inviteRepository;
    @Mock private MembershipRepository membershipRepository;

    @InjectMocks private RevokeInvite revokeInvite;

    private User admin;
    private User member;
    private Account account;
    private Invite invite;

    @BeforeEach
    void setUp() {
        admin = new User("Admin", "admin@test.com", "hash");
        member = new User("Member", "member@test.com", "hash");
        account = new Account("Conta Teste");
        invite = new Invite("target@test.com", LocalDate.now().plusDays(7), account, Role.MEMBER);
    }

    @Test
    void shouldRevokeInviteWhenAdmin() {
        when(inviteRepository.findById(1L)).thenReturn(Optional.of(invite));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(new Membership(account, admin, Role.ADMIN)));

        revokeInvite.execute(1L, admin);

        assertThat(invite.getStatus()).isEqualTo(InviteStatus.REVOKED);
        verify(inviteRepository).save(invite);
    }

    @Test
    void shouldRevokeInviteWhenOwner() {
        User owner = new User("Owner", "owner@test.com", "hash");
        when(inviteRepository.findById(1L)).thenReturn(Optional.of(invite));
        when(membershipRepository.findByAccountAndUser(account, owner))
                .thenReturn(Optional.of(new Membership(account, owner, Role.OWNER)));

        revokeInvite.execute(1L, owner);

        assertThat(invite.getStatus()).isEqualTo(InviteStatus.REVOKED);
    }

    @Test
    void shouldThrowWhenInviteNotFound() {
        when(inviteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> revokeInvite.execute(99L, admin))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowForbiddenWhenNoMembership() {
        when(inviteRepository.findById(1L)).thenReturn(Optional.of(invite));
        when(membershipRepository.findByAccountAndUser(account, admin)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> revokeInvite.execute(1L, admin))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void shouldThrowForbiddenWhenMemberCannotRevoke() {
        when(inviteRepository.findById(1L)).thenReturn(Optional.of(invite));
        when(membershipRepository.findByAccountAndUser(account, member))
                .thenReturn(Optional.of(new Membership(account, member, Role.MEMBER)));

        assertThatThrownBy(() -> revokeInvite.execute(1L, member))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void shouldThrowWhenInviteAlreadyAccepted() {
        User acceptingUser = new User("Accepted", "target@test.com", "hash");
        invite.accept(acceptingUser);
        when(inviteRepository.findById(1L)).thenReturn(Optional.of(invite));
        when(membershipRepository.findByAccountAndUser(account, admin))
                .thenReturn(Optional.of(new Membership(account, admin, Role.ADMIN)));

        assertThatThrownBy(() -> revokeInvite.execute(1L, admin))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pendente");
    }
}
