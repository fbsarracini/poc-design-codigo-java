package io.github.fbsarracini.javadesign.project;

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

@UseCase
public class CreateNewProject {

    private static final Logger log = LoggerFactory.getLogger(CreateNewProject.class);

    private final AccountRepository accountRepository;
    private final MembershipRepository membershipRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMembershipRepository projectMembershipRepository;

    public CreateNewProject(AccountRepository accountRepository,
                            MembershipRepository membershipRepository,
                            ProjectRepository projectRepository,
                            ProjectMembershipRepository projectMembershipRepository) {
        this.accountRepository = accountRepository;
        this.membershipRepository = membershipRepository;
        this.projectRepository = projectRepository;
        this.projectMembershipRepository = projectMembershipRepository;
    }

    @Transactional
    public Project execute(@Positive Long accountId, User loggedUser, @Valid NewProjectData newProjectData) {
        AppLog.logger(log).who(loggedUser).does("criar projeto").on("account=" + accountId).debug();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(NotFoundException::new);

        membershipRepository.findByAccountAndUser(account, loggedUser)
                .filter(Membership::canCreateProject)
                .orElseThrow(() -> {
                    AppLog.logger(log).who(loggedUser).does("criar projeto").on("account=" + accountId).failed("sem permissão para criar projetos").warn();
                    return new ForbiddenException();
                });

        AppLog.logger(log).who(loggedUser).does("criar projeto").on("account=" + accountId).info();
        Project project = projectRepository.save(newProjectData.toNewProject(account));
        AppLog.logger(log).who(loggedUser).does("criar projeto membership").on("project=" + project.getId()).info();
        projectMembershipRepository.save(new ProjectMembership(project, loggedUser));
        AppLog.logger(log).who(loggedUser).does("criar projeto").on("project=" + project.getId() + " account=" + accountId).info();
        return project;
    }
}
