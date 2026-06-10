package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.AccountRepository;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.log.AppLog;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@UseCase
public class ListAccountProjects {

    private static final Logger log = LoggerFactory.getLogger(ListAccountProjects.class);

    private final AccountRepository accountRepository;
    private final MembershipRepository membershipRepository;
    private final ProjectRepository projectRepository;

    public ListAccountProjects(AccountRepository accountRepository,
                               MembershipRepository membershipRepository,
                               ProjectRepository projectRepository) {
        this.accountRepository = accountRepository;
        this.membershipRepository = membershipRepository;
        this.projectRepository = projectRepository;
    }

    public List<ProjectSummaryResponse> execute(@Positive Long accountId, User loggedUser) {
        AppLog.logger(log).who(loggedUser).does("listar projetos").on("account=" + accountId).debug();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(NotFoundException::new);

        if (membershipRepository.findByAccountAndUser(account, loggedUser).isEmpty()) {
            AppLog.logger(log).who(loggedUser).does("listar projetos").on("account=" + accountId).failed("sem membership na conta").warn();
            throw new ForbiddenException();
        }

        return projectRepository.findByAccount(account)
                .stream()
                .map(ProjectSummaryResponse::of)
                .toList();
    }
}
