package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.user.User;
import org.junit.jupiter.api.BeforeEach;
import static io.github.fbsarracini.javadesign.TestFixtures.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListMyAccountsTest {

    @Mock private MembershipRepository membershipRepository;

    @InjectMocks private ListMyAccounts listMyAccounts;

    private User user;

    @BeforeEach
    void setUp() {
        user = user("User", "user@test.com");
    }

    @Test
    void shouldReturnAccountsForUser() {
        Account a1 = account("Conta 1");
        Account a2 = account("Conta 2");
        Membership m1 = membershipAs(a1, user, Role.OWNER);
        Membership m2 = membershipAs(a2, user, Role.MEMBER);
        when(membershipRepository.findByUser(user)).thenReturn(List.of(m1, m2));

        List<AccountSummaryResponse> result = listMyAccounts.execute(user);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(AccountSummaryResponse::name)
                .containsExactly("Conta 1", "Conta 2");
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoAccounts() {
        when(membershipRepository.findByUser(user)).thenReturn(List.of());

        List<AccountSummaryResponse> result = listMyAccounts.execute(user);

        assertThat(result).isEmpty();
    }
}
