package io.github.fbsarracini.javadesign.account;

public record MemberSummaryResponse(Long userId, String name, String email, Role role) {

    public static MemberSummaryResponse of(Membership membership) {
        return new MemberSummaryResponse(
                membership.getUser().getId(),
                membership.getUser().getName(),
                membership.getUser().getUsername(),
                membership.getRole()
        );
    }
}
