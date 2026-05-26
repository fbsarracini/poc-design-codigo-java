package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.AccountRepository;
import io.github.fbsarracini.javadesign.user.User;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@UseCase
public class GenerateNewInvite {

    private final AccountRepository accountRepository;
    private final InviteRepository inviteRepository;

    public GenerateNewInvite(AccountRepository accountRepository, InviteRepository inviteRepository) {
        this.accountRepository = accountRepository;
        this.inviteRepository = inviteRepository;
    }

    @Transactional
    public void execute(@Positive Long accountId, User loggedUser, @Valid NewInviteData newInviteData) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(NotFoundException::new);

        if (!account.belongsTo(loggedUser)) {
            throw new ForbiddenException();
        }

        Invite newInvite = newInviteData.toNewInvite(account);

        inviteRepository.save(newInvite);
    }

}
