package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.transaction.Transactional;

@UseCase
public class AcceptInvite {

    private final InviteRepository inviteRepository;
    private final MembershipRepository membershipRepository;

    public AcceptInvite(InviteRepository inviteRepository, MembershipRepository membershipRepository) {
        this.inviteRepository = inviteRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public void execute(String token, User loggedUser) {
        Invite invite = inviteRepository.findByToken(token)
                .orElseThrow(NotFoundException::new);

        if (membershipRepository.findByAccountAndUser(invite.getAccount(), loggedUser).isPresent()) {
            throw new IllegalArgumentException("usuário já é membro desta conta");
        }

        Membership membership = invite.accept(loggedUser);

        inviteRepository.save(invite);
        membershipRepository.save(membership);
    }
}
