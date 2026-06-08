package io.github.fbsarracini.javadesign.account;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record NewAccountRequest(
        @NotBlank String organization
) implements @Valid NewAccountData {

    @Override
    public Account toNewAccount() {
        return new Account(organization);
    }
}
