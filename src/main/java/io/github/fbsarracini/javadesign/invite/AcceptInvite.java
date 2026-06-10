package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.log.AppLog;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UseCase
public class AcceptInvite {

    private static final Logger log = LoggerFactory.getLogger(AcceptInvite.class);

    private final InviteRepository inviteRepository;
    private final MembershipRepository membershipRepository;

    public AcceptInvite(InviteRepository inviteRepository, MembershipRepository membershipRepository) {
        this.inviteRepository = inviteRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public void execute(String token, User loggedUser) {
        AppLog.logger(log).who(loggedUser).does("aceitar convite").on("token=" + token).debug();
        Invite invite = inviteRepository.findByToken(token)
                .orElseThrow(NotFoundException::new);

        if (membershipRepository.findByAccountAndUser(invite.getAccount(), loggedUser).isPresent()) {
            AppLog.logger(log).who(loggedUser).does("aceitar convite").on("account=" + invite.getAccount().getId()).failed("usuário já é membro").warn();
            throw new IllegalArgumentException("usuário já é membro desta conta");
        }

        Membership membership;
        try {
            membership = invite.accept(loggedUser);
        } catch (IllegalArgumentException e) {
            AppLog.logger(log).who(loggedUser).does("aceitar convite").on("invite=" + invite.getId() + " account=" + invite.getAccount().getId()).failed(e.getMessage()).warn();
            throw e;
        }

        AppLog.logger(log).who(loggedUser).does("aceitar convite").on("invite=" + invite.getId()).info();
        inviteRepository.save(invite);
        AppLog.logger(log).who(loggedUser).does("aceitar convite membership").on("account=" + invite.getAccount().getId()).info();
        membershipRepository.save(membership);
        AppLog.logger(log).who(loggedUser).does("aceitar convite").on("account=" + invite.getAccount().getId()).info();
    }
}
