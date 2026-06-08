package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.user.User;
import io.github.fbsarracini.javadesign.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@UseCase
public class AddUserToProject {

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
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NotFoundException::new);

        membershipRepository.findByAccountAndUser(project.getAccount(), loggedUser)
                .filter(Membership::isAdminOrAbove)
                .orElseThrow(ForbiddenException::new);

        User targetUser = userRepository.findByEmail(targetEmail)
                .orElseThrow(NotFoundException::new);

        if (membershipRepository.findByAccountAndUser(project.getAccount(), targetUser).isEmpty()) {
            throw new IllegalArgumentException("o usuário não é membro desta conta");
        }

        if (!projectMembershipRepository.existsByProjectAndUser(project, targetUser)) {
            projectMembershipRepository.save(new ProjectMembership(project, targetUser));
        }
    }
}
