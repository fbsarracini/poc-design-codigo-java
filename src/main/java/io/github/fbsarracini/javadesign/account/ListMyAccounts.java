package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.UseCase;
import io.github.fbsarracini.javadesign.user.User;

import java.util.List;

@UseCase
public class ListMyAccounts {

    private final MembershipRepository membershipRepository;

    public ListMyAccounts(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public List<AccountSummaryResponse> execute(User loggedUser) {
        return membershipRepository.findByUser(loggedUser)
                .stream()
                .map(AccountSummaryResponse::of)
                .toList();
    }
}
