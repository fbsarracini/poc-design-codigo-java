package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.account.Account;

public interface NewProjectData {
    Project toNewProject(Account account);
}
