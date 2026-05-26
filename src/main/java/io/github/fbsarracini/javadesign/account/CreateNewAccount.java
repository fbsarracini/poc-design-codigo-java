package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@UseCase
public class CreateNewAccount {

    private final AccountRepository accountRepository;

    public CreateNewAccount(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account execute(@AuthenticationPrincipal User loggedUser, @Valid NewAccountData newAccountData) {
        return accountRepository.save(newAccountData.toNewAccount(loggedUser));
    }
}
