package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.log.AppLog;
import io.github.fbsarracini.javadesign.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@UseCase
public class ListMyAccounts {

    private static final Logger log = LoggerFactory.getLogger(ListMyAccounts.class);

    private final MembershipRepository membershipRepository;

    public ListMyAccounts(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public List<AccountSummaryResponse> execute(User loggedUser) {
        AppLog.logger(log).who(loggedUser).does("listar contas").info();
        List<AccountSummaryResponse> result = membershipRepository.findByUser(loggedUser)
                .stream()
                .map(AccountSummaryResponse::of)
                .toList();
        AppLog.logger(log).who(loggedUser).does("listar contas").info();
        return result;
    }
}
