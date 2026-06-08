package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.AccountRepository;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.constraints.Positive;

import java.util.List;

@UseCase
public class ListAccountProjects {

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
        Account account = accountRepository.findById(accountId)
                .orElseThrow(NotFoundException::new);

        if (membershipRepository.findByAccountAndUser(account, loggedUser).isEmpty()) {
            throw new ForbiddenException();
        }

        return projectRepository.findByAccount(account)
                .stream()
                .map(ProjectSummaryResponse::of)
                .toList();
    }
}
