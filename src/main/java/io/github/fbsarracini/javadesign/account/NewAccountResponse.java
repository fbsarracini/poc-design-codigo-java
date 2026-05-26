package io.github.fbsarracini.javadesign.account;

public record NewAccountResponse(Long id) {

    public static NewAccountResponse of(Account account) {
        return new NewAccountResponse(account.getId());
    }
}
