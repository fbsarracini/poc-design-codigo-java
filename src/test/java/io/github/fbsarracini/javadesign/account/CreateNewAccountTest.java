package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateNewAccountTest {

    @Mock private AccountRepository accountRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private NewAccountData newAccountData;

    @InjectMocks private CreateNewAccount createNewAccount;

    private User owner;
    private Account account;

    @BeforeEach
    void setUp() {
        owner = new User("Owner", "owner@test.com", "hash");
        account = new Account("Minha Conta");
        when(newAccountData.toNewAccount()).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
    }

    @Test
    void shouldCreateAccountAndAddOwnerMembership() {
        Account result = createNewAccount.execute(owner, newAccountData);

        assertEquals(account, result);
        verify(accountRepository).save(account);
        verify(membershipRepository).save(any(Membership.class));
    }
}
