package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.log.AppLog;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UseCase
public class RevokeInvite {

    private static final Logger log = LoggerFactory.getLogger(RevokeInvite.class);

    private final InviteRepository inviteRepository;
    private final MembershipRepository membershipRepository;

    public RevokeInvite(InviteRepository inviteRepository, MembershipRepository membershipRepository) {
        this.inviteRepository = inviteRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public void execute(@Positive Long inviteId, User loggedUser) {
        AppLog.logger(log).who(loggedUser).does("revogar convite").on("invite=" + inviteId).debug();
        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(NotFoundException::new);

        membershipRepository.findByAccountAndUser(invite.getAccount(), loggedUser)
                .filter(Membership::canInvite)
                .orElseThrow(() -> {
                    AppLog.logger(log).who(loggedUser).does("revogar convite").on("invite=" + inviteId).failed("sem permissão para gerenciar convites").warn();
                    return new ForbiddenException();
                });

        invite.revoke();
        AppLog.logger(log).who(loggedUser).does("revogar convite").on("invite=" + inviteId).info();
        inviteRepository.save(invite);
        AppLog.logger(log).who(loggedUser).does("revogar convite").on("invite=" + inviteId).info();
    }
}
