package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record NewInviteRequest(
        @Email @NotBlank String email,
        @Min(1) @Max(90) @NotNull Integer daysToExpire,
        @NotNull Role role
) implements @Valid NewInviteData {

    @Override
    public Invite toNewInvite(Account account) {
        return new Invite(email, LocalDate.now().plusDays(daysToExpire), account, role);
    }
}
