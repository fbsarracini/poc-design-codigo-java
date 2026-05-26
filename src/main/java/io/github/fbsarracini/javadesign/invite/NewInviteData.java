package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.account.Account;

public interface NewInviteData {

    Invite toNewInvite(Account account);
}
