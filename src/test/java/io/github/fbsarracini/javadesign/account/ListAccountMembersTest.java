package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAccountMembersTest {

    @Mock private AccountRepository accountRepository;
    @Mock private MembershipRepository membershipRepository;

    @InjectMocks private ListAccountMembers listAccountMembers;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("Conta Teste");
        user = new User("User", "user@test.com", "hash");
    }

    @Test
    void shouldReturnMembersForMember() {
        User other = new User("Other", "other@test.com", "hash");
        Membership m1 = new Membership(account, user, Role.MEMBER);
        Membership m2 = new Membership(account, other, Role.ADMIN);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, user))
                .thenReturn(Optional.of(m1));
        when(membershipRepository.findByAccount(account)).thenReturn(List.of(m1, m2));

        List<MemberSummaryResponse> result = listAccountMembers.execute(1L, user);

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listAccountMembers.execute(99L, user))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowForbiddenWhenNotMember() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listAccountMembers.execute(1L, user))
                .isInstanceOf(ForbiddenException.class);
    }
}
