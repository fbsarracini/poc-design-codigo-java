package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.AccountRepository;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.log.AppLog;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@UseCase
public class GenerateNewInvite {

    private static final Logger log = LoggerFactory.getLogger(GenerateNewInvite.class);

    private final AccountRepository accountRepository;
    private final MembershipRepository membershipRepository;
    private final InviteRepository inviteRepository;

    public GenerateNewInvite(AccountRepository accountRepository,
                             MembershipRepository membershipRepository,
                             InviteRepository inviteRepository) {
        this.accountRepository = accountRepository;
        this.membershipRepository = membershipRepository;
        this.inviteRepository = inviteRepository;
    }

    @Transactional
    public void execute(@Positive Long accountId, User loggedUser, @Valid NewInviteData newInviteData) {
        AppLog.logger(log).who(loggedUser).does("gerar convite").on("account=" + accountId).debug();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(NotFoundException::new);

        membershipRepository.findByAccountAndUser(account, loggedUser)
                .filter(Membership::canInvite)
                .orElseThrow(() -> {
                    AppLog.logger(log).who(loggedUser).does("gerar convite").on("account=" + accountId).failed("sem permissão para convidar").warn();
                    return new ForbiddenException();
                });

        if (inviteRepository.existsByAccountAndEmailAndStatusAndExpirationDateGreaterThanEqual(
                account, newInviteData.email(), InviteStatus.PENDING, LocalDate.now())) {
            AppLog.logger(log).who(loggedUser).does("gerar convite").on("email=" + newInviteData.email()).failed("convite pendente já existe").warn();
            throw new IllegalArgumentException("já existe um convite pendente para este email");
        }

        AppLog.logger(log).who(loggedUser).does("gerar convite").on("account=" + accountId + " email=" + newInviteData.email()).info();
        inviteRepository.save(newInviteData.toNewInvite(account));
        AppLog.logger(log).who(loggedUser).does("gerar convite").on("account=" + accountId + " email=" + newInviteData.email()).info();
    }
}
