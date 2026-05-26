package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.user.User;

public interface NewAccountData {
    public Account toNewAccount(User owner);
}
