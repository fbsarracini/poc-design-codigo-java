package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record NewAccountRequest(
        @NotBlank String organization
) implements @Valid NewAccountData {

    @Override
    public Account toNewAccount(User owner) {
        return new Account(organization, owner);
    }
}
