package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.constraints.Positive;

import java.util.List;

@UseCase
public class ListAccountMembers {

    private final AccountRepository accountRepository;
    private final MembershipRepository membershipRepository;

    public ListAccountMembers(AccountRepository accountRepository, MembershipRepository membershipRepository) {
        this.accountRepository = accountRepository;
        this.membershipRepository = membershipRepository;
    }

    public List<MemberSummaryResponse> execute(@Positive Long accountId, User loggedUser) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(NotFoundException::new);

        if (membershipRepository.findByAccountAndUser(account, loggedUser).isEmpty()) {
            throw new ForbiddenException();
        }

        return membershipRepository.findByAccount(account)
                .stream()
                .map(MemberSummaryResponse::of)
                .toList();
    }
}
