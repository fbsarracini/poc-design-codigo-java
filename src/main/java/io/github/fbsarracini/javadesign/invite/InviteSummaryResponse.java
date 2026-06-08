package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.account.Role;

import java.time.LocalDate;

public record InviteSummaryResponse(Long id, String email, Role role, LocalDate expirationDate, String token) {

    public static InviteSummaryResponse of(Invite invite) {
        return new InviteSummaryResponse(
                invite.getId(),
                invite.getEmail(),
                invite.getRole(),
                invite.getExpirationDate(),
                invite.getToken()
        );
    }
}
