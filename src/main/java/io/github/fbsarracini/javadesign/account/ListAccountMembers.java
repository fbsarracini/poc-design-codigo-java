package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.log.AppLog;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@UseCase
public class ListAccountMembers {

    private static final Logger log = LoggerFactory.getLogger(ListAccountMembers.class);

    private final AccountRepository accountRepository;
    private final MembershipRepository membershipRepository;

    public ListAccountMembers(AccountRepository accountRepository, MembershipRepository membershipRepository) {
        this.accountRepository = accountRepository;
        this.membershipRepository = membershipRepository;
    }

    public List<MemberSummaryResponse> execute(@Positive Long accountId, User loggedUser) {
        AppLog.logger(log).who(loggedUser).does("listar membros").on("account=" + accountId).debug();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(NotFoundException::new);

        if (membershipRepository.findByAccountAndUser(account, loggedUser).isEmpty()) {
            AppLog.logger(log).who(loggedUser).does("listar membros").on("account=" + accountId).failed("sem membership na conta").warn();
            throw new ForbiddenException();
        }

        AppLog.logger(log).who(loggedUser).does("listar membros").on("account=" + accountId).debug();
        List<MemberSummaryResponse> result = membershipRepository.findByAccount(account)
                .stream()
                .map(MemberSummaryResponse::of)
                .toList();
        AppLog.logger(log).who(loggedUser).does("listar membros").on("account=" + accountId).info();
        return result;
    }
}
