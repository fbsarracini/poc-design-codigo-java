package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.account.Role;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcceptInviteTest {

    @Mock private InviteRepository inviteRepository;
    @Mock private MembershipRepository membershipRepository;

    @InjectMocks private AcceptInvite acceptInvite;

    private User user;
    private Account account;
    private Invite invite;

    @BeforeEach
    void setUp() {
        account = account("Conta Teste");
        user = user("João", "joao@test.com");
        invite = pendingInvite("joao@test.com", account, Role.MEMBER);
    }

    @Test
    void shouldAcceptInviteSuccessfully() {
        when(inviteRepository.findByToken("tok")).thenReturn(Optional.of(invite));
        when(membershipRepository.findByAccountAndUser(account, user)).thenReturn(Optional.empty());

        acceptInvite.execute("tok", user);

        assertThat(invite.getStatus()).isEqualTo(InviteStatus.ACCEPTED);
        verify(inviteRepository).save(invite);

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(membershipRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isEqualTo(user);
        assertThat(captor.getValue().getRole()).isEqualTo(Role.MEMBER);
    }

    @Test
    void shouldThrowWhenTokenNotFound() {
        when(inviteRepository.findByToken("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> acceptInvite.execute("invalid", user))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowWhenUserAlreadyMember() {
        when(inviteRepository.findByToken("tok")).thenReturn(Optional.of(invite));
        when(membershipRepository.findByAccountAndUser(account, user))
                .thenReturn(Optional.of(membershipAs(account, user, Role.MEMBER)));

        assertThatThrownBy(() -> acceptInvite.execute("tok", user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("membro");

        verify(inviteRepository, never()).save(any(Invite.class));
        verify(membershipRepository, never()).save(any(Membership.class));
    }

    @Test
    void shouldThrowWhenInviteIsRevoked() {
        invite.revoke();
        when(inviteRepository.findByToken("tok")).thenReturn(Optional.of(invite));
        when(membershipRepository.findByAccountAndUser(account, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> acceptInvite.execute("tok", user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("utilizado");

        verify(inviteRepository, never()).save(any(Invite.class));
        verify(membershipRepository, never()).save(any(Membership.class));
    }

    @Test
    void shouldThrowWhenEmailDoesNotMatch() {
        Invite inviteForOther = pendingInvite("outro@test.com", account, Role.MEMBER);
        when(inviteRepository.findByToken("tok")).thenReturn(Optional.of(inviteForOther));
        when(membershipRepository.findByAccountAndUser(account, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> acceptInvite.execute("tok", user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");

        verify(inviteRepository, never()).save(any(Invite.class));
        verify(membershipRepository, never()).save(any(Membership.class));
    }
}
