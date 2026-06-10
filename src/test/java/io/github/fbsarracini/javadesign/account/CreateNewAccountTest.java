package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateNewAccountTest {

    @Mock private AccountRepository accountRepository;
    @Mock private MembershipRepository membershipRepository;

    @InjectMocks private CreateNewAccount createNewAccount;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User("Owner", "owner@test.com", "hash");
    }

    @Test
    void shouldCreateAccountAndAddOwnerMembership() {
        NewAccountRequest request = new NewAccountRequest("Minha Conta");
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account result = createNewAccount.execute(owner, request);

        assertThat(result.getName()).isEqualTo("Minha Conta");
        verify(accountRepository).save(any(Account.class));

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(membershipRepository).save(captor.capture());
        Membership saved = captor.getValue();
        assertThat(saved.getAccount()).isSameAs(result);
        assertThat(saved.getUser()).isEqualTo(owner);
        assertThat(saved.getRole()).isEqualTo(Role.OWNER);
    }

    @Test
    void shouldThrowWhenOrganizationNameIsBlank() {
        NewAccountRequest request = new NewAccountRequest("");

        assertThatThrownBy(() -> createNewAccount.execute(owner, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatório");
    }
}
