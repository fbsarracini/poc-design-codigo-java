package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.Role;
import io.github.fbsarracini.javadesign.user.User;
import org.junit.jupiter.api.BeforeEach;
import static io.github.fbsarracini.javadesign.TestFixtures.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InviteTest {

    private Account account;
    private User user;

    @BeforeEach
    void setUp() {
        account = account("Conta Teste");
        user = user("João", "joao@test.com");
    }

    @Test
    @DisplayName("deve criar convite com status pendente")
    void shouldCreatePendingInvite() {
        Invite invite = new Invite("joao@test.com", LocalDate.now().plusDays(7), account, Role.MEMBER);

        assertThat(invite.getStatus()).isEqualTo(InviteStatus.PENDING);
        assertThat(invite.getEmail()).isEqualTo("joao@test.com");
        assertThat(invite.getRole()).isEqualTo(Role.MEMBER);
    }

    @Test
    @DisplayName("deve lançar exceção quando data de expiração é hoje")
    void shouldThrowWhenExpirationDateIsToday() {
        assertThatThrownBy(() -> new Invite("joao@test.com", LocalDate.now(), account, Role.MEMBER))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("deve lançar exceção quando data de expiração é no passado")
    void shouldThrowWhenExpirationDateIsInPast() {
        assertThatThrownBy(() -> new Invite("joao@test.com", LocalDate.now().minusDays(1), account, Role.MEMBER))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("deve lançar exceção quando role é Owner")
    void shouldThrowWhenRoleIsOwner() {
        assertThatThrownBy(() -> new Invite("joao@test.com", LocalDate.now().plusDays(7), account, Role.OWNER))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("deve aceitar convite com sucesso")
    void shouldAcceptInviteSuccessfully() {
        Invite invite = new Invite("joao@test.com", LocalDate.now().plusDays(7), account, Role.MEMBER);

        Membership membership = invite.accept(user);

        assertThat(invite.getStatus()).isEqualTo(InviteStatus.ACCEPTED);
        assertThat(membership.getUser()).isEqualTo(user);
        assertThat(membership.getAccount()).isEqualTo(account);
        assertThat(membership.getRole()).isEqualTo(Role.MEMBER);
    }

    @Test
    @DisplayName("deve lançar exceção ao aceitar convite já aceito")
    void shouldThrowWhenAcceptingAlreadyAcceptedInvite() {
        Invite invite = new Invite("joao@test.com", LocalDate.now().plusDays(7), account, Role.MEMBER);
        invite.accept(user);

        assertThatThrownBy(() -> invite.accept(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("utilizado");
    }

    @Test
    @DisplayName("deve lançar exceção ao aceitar convite revogado")
    void shouldThrowWhenAcceptingRevokedInvite() {
        Invite invite = new Invite("joao@test.com", LocalDate.now().plusDays(7), account, Role.MEMBER);
        invite.revoke();

        assertThatThrownBy(() -> invite.accept(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("utilizado");
    }

    @Test
    @DisplayName("deve lançar exceção ao aceitar convite expirado")
    void shouldThrowWhenAcceptingExpiredInvite() throws Exception {
        Invite invite = new Invite("joao@test.com", LocalDate.now().plusDays(1), account, Role.MEMBER);
        Field field = Invite.class.getDeclaredField("expirationDate");
        field.setAccessible(true);
        field.set(invite, LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> invite.accept(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expirado");
    }

    @Test
    @DisplayName("deve lançar exceção ao aceitar com email incorreto")
    void shouldThrowWhenAcceptingWithWrongEmail() {
        Invite invite = new Invite("outro@test.com", LocalDate.now().plusDays(7), account, Role.MEMBER);

        assertThatThrownBy(() -> invite.accept(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    @DisplayName("deve revogar convite com sucesso")
    void shouldRevokeInviteSuccessfully() {
        Invite invite = new Invite("joao@test.com", LocalDate.now().plusDays(7), account, Role.MEMBER);

        invite.revoke();

        assertThat(invite.getStatus()).isEqualTo(InviteStatus.REVOKED);
    }

    @Test
    @DisplayName("deve lançar exceção ao revogar convite já aceito")
    void shouldThrowWhenRevokingAcceptedInvite() {
        Invite invite = new Invite("joao@test.com", LocalDate.now().plusDays(7), account, Role.MEMBER);
        invite.accept(user);

        assertThatThrownBy(() -> invite.revoke())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pendente");
    }

    @Test
    @DisplayName("deve lançar exceção ao revogar convite já revogado")
    void shouldThrowWhenRevokingAlreadyRevokedInvite() {
        Invite invite = new Invite("joao@test.com", LocalDate.now().plusDays(7), account, Role.MEMBER);
        invite.revoke();

        assertThatThrownBy(() -> invite.revoke())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pendente");
    }
}
