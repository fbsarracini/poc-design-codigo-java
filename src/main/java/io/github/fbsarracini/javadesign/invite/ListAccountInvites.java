package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.AccountRepository;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

@UseCase
public class ListAccountInvites {

    private final AccountRepository accountRepository;
    private final MembershipRepository membershipRepository;
    private final InviteRepository inviteRepository;

    public ListAccountInvites(AccountRepository accountRepository,
                              MembershipRepository membershipRepository,
                              InviteRepository inviteRepository) {
        this.accountRepository = accountRepository;
        this.membershipRepository = membershipRepository;
        this.inviteRepository = inviteRepository;
    }

    public List<InviteSummaryResponse> execute(@Positive Long accountId, User loggedUser) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(NotFoundException::new);

        membershipRepository.findByAccountAndUser(account, loggedUser)
                .filter(Membership::canInvite)
                .orElseThrow(ForbiddenException::new);

        return inviteRepository.findByAccountAndStatusAndExpirationDateGreaterThanEqual(account, InviteStatus.PENDING, LocalDate.now())
                .stream()
                .map(InviteSummaryResponse::of)
                .toList();
    }
}
