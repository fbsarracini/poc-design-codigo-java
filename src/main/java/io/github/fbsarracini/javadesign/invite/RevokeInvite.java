package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;

@UseCase
public class RevokeInvite {

    private final InviteRepository inviteRepository;
    private final MembershipRepository membershipRepository;

    public RevokeInvite(InviteRepository inviteRepository, MembershipRepository membershipRepository) {
        this.inviteRepository = inviteRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public void execute(@Positive Long inviteId, User loggedUser) {
        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(NotFoundException::new);

        membershipRepository.findByAccountAndUser(invite.getAccount(), loggedUser)
                .filter(Membership::canInvite)
                .orElseThrow(ForbiddenException::new);

        invite.revoke();
        inviteRepository.save(invite);
    }
}
