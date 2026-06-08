package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@UseCase
public class CreateNewAccount {

    private final AccountRepository accountRepository;
    private final MembershipRepository membershipRepository;

    public CreateNewAccount(AccountRepository accountRepository, MembershipRepository membershipRepository) {
        this.accountRepository = accountRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public Account execute(User loggedUser, @Valid NewAccountData newAccountData) {
        Account account = accountRepository.save(newAccountData.toNewAccount());
        membershipRepository.save(new Membership(account, loggedUser, Role.OWNER));
        return account;
    }
}
