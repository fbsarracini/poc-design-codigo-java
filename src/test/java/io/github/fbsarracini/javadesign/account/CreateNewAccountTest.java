package io.github.fbsarracini.javadesign.account;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateNewAccountTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private MembershipRepository membershipRepository;

    @InjectMocks
    private CreateNewAccount createNewAccount;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = user("Owner", "owner@test.com");
    }

    @Test
    @DisplayName("deve criar conta e adicionar membership de owner")
    void shouldCreateAccountAndAddOwnerMembership() {
        NewAccountRequest request = new NewAccountRequest("Minha Conta");
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        when(accountRepository.save(accountCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        Account result = createNewAccount.execute(owner, request);

        assertThat(result.getName()).isEqualTo("Minha Conta");
        verify(accountRepository).save(result);

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(membershipRepository).save(captor.capture());
        Membership saved = captor.getValue();
        assertThat(saved.getAccount()).isSameAs(result);
        assertThat(saved.getUser()).isEqualTo(owner);
        assertThat(saved.getRole()).isEqualTo(Role.OWNER);
    }

    @Test
    @DisplayName("deve lançar exceção quando nome da organização é vazio")
    void shouldThrowWhenOrganizationNameIsBlank() {
        NewAccountRequest request = new NewAccountRequest("");

        assertThatThrownBy(() -> createNewAccount.execute(owner, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatório");
    }

    @Test
    @DisplayName("deve lançar exceção quando nome da organização é nulo")
    void shouldThrowWhenOrganizationNameIsNull() {
        NewAccountRequest request = new NewAccountRequest(null);

        assertThatThrownBy(() -> createNewAccount.execute(owner, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatório");
    }

    @Test
    @DisplayName("deve lançar exceção quando nome da organização é apenas espaços")
    void shouldThrowWhenOrganizationNameIsWhitespace() {
        NewAccountRequest request = new NewAccountRequest("   ");

        assertThatThrownBy(() -> createNewAccount.execute(owner, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatório");
    }
}
