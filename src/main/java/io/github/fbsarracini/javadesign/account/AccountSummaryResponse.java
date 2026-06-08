package io.github.fbsarracini.javadesign.account;

public record AccountSummaryResponse(Long id, String name, Role role) {

    public static AccountSummaryResponse of(Membership membership) {
        return new AccountSummaryResponse(
                membership.getAccount().getId(),
                membership.getAccount().getName(),
                membership.getRole()
        );
    }
}
