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
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@UseCase
public class ListAccountInvites {

    private static final Logger log = LoggerFactory.getLogger(ListAccountInvites.class);

    private final AccountRepository accountRepository;
    private final MembershipRepository membershipRepository;
    private final InviteRepository inviteRepository;

    public ListAccountInvites(AccountRepository accountRepository,
                              MembershipRepository membershipRepository,
                              InviteRepository inviteRepository) {
        this.accountRepository = accountRepository;
        this.membershipRepository = membershipRepository;
        this.inviteRepository = inviteRepository;
    }

    public List<InviteSummaryResponse> execute(@Positive Long accountId, User loggedUser) {
        AppLog.logger(log).who(loggedUser).does("listar convites").on("account=" + accountId).debug();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(NotFoundException::new);

        membershipRepository.findByAccountAndUser(account, loggedUser)
                .filter(Membership::canInvite)
                .orElseThrow(() -> {
                    AppLog.logger(log).who(loggedUser).does("listar convites").on("account=" + accountId).failed("sem permissão para gerenciar convites").warn();
                    return new ForbiddenException();
                });

        AppLog.logger(log).who(loggedUser).does("listar convites").on("account=" + accountId).debug();
        List<InviteSummaryResponse> result = inviteRepository.findByAccountAndStatusAndExpirationDateGreaterThanEqual(account, InviteStatus.PENDING, LocalDate.now())
                .stream()
                .map(InviteSummaryResponse::of)
                .toList();
        AppLog.logger(log).who(loggedUser).does("listar convites").on("account=" + accountId).info();
        return result;
    }
}
