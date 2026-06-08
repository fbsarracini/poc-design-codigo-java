package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.AccountRepository;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@UseCase
public class CreateNewProject {

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
        Account account = accountRepository.findById(accountId)
                .orElseThrow(NotFoundException::new);

        membershipRepository.findByAccountAndUser(account, loggedUser)
                .filter(Membership::canCreateProject)
                .orElseThrow(ForbiddenException::new);

        Project project = projectRepository.save(newProjectData.toNewProject(account));
        projectMembershipRepository.save(new ProjectMembership(project, loggedUser));
        return project;
    }
}
