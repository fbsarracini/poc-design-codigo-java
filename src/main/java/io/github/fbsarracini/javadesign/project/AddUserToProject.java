package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.log.AppLog;
import io.github.fbsarracini.javadesign.user.User;
import io.github.fbsarracini.javadesign.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UseCase
public class AddUserToProject {

    private static final Logger log = LoggerFactory.getLogger(AddUserToProject.class);

    private final ProjectRepository projectRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final ProjectMembershipRepository projectMembershipRepository;

    public AddUserToProject(ProjectRepository projectRepository,
                            MembershipRepository membershipRepository,
                            UserRepository userRepository,
                            ProjectMembershipRepository projectMembershipRepository) {
        this.projectRepository = projectRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.projectMembershipRepository = projectMembershipRepository;
    }

    @Transactional
    public void execute(@Positive Long projectId, User loggedUser, @NotBlank @Email String targetEmail) {
        AppLog.logger(log).who(loggedUser).does("adicionar usuário ao projeto").on("project=" + projectId + " target=" + targetEmail).debug();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NotFoundException::new);

        membershipRepository.findByAccountAndUser(project.getAccount(), loggedUser)
                .filter(Membership::isAdminOrAbove)
                .orElseThrow(() -> {
                    AppLog.logger(log).who(loggedUser).does("adicionar usuário ao projeto").on("project=" + projectId).failed("sem permissão de admin").warn();
                    return new ForbiddenException();
                });

        AppLog.logger(log).who(loggedUser).does("adicionar usuário ao projeto").on("target=" + targetEmail).debug();
        User targetUser = userRepository.findByEmail(targetEmail)
                .orElseThrow(NotFoundException::new);

        if (membershipRepository.findByAccountAndUser(project.getAccount(), targetUser).isEmpty()) {
            AppLog.logger(log).who(loggedUser).does("adicionar usuário ao projeto").on("project=" + projectId + " target=" + targetEmail).failed("target não é membro da conta").warn();
            throw new IllegalArgumentException("o usuário não é membro desta conta");
        }

        if (!projectMembershipRepository.existsByProjectAndUser(project, targetUser)) {
            projectMembershipRepository.save(new ProjectMembership(project, targetUser));
            AppLog.logger(log).who(loggedUser).does("adicionar usuário ao projeto").on("project=" + projectId + " target=" + targetEmail).info();
        }
    }
}
