package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.log.AppLog;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UseCase
public class CreateNewAccount {

    private static final Logger log = LoggerFactory.getLogger(CreateNewAccount.class);

    private final AccountRepository accountRepository;
    private final MembershipRepository membershipRepository;

    public CreateNewAccount(AccountRepository accountRepository, MembershipRepository membershipRepository) {
        this.accountRepository = accountRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public Account execute(User loggedUser, @Valid NewAccountData newAccountData) {
        AppLog.logger(log).who(loggedUser).does("criar conta").info();
        Account account = accountRepository.save(newAccountData.toNewAccount());
        AppLog.logger(log).who(loggedUser).does("criar conta").on("account=" + account.getId()).info();
        membershipRepository.save(new Membership(account, loggedUser, Role.OWNER));
        return account;
    }
}
