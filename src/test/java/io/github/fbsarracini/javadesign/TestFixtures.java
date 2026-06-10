package io.github.fbsarracini.javadesign;

import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.Role;
import io.github.fbsarracini.javadesign.invite.Invite;
import io.github.fbsarracini.javadesign.project.Project;
import io.github.fbsarracini.javadesign.todo.Todo;
import io.github.fbsarracini.javadesign.user.User;

import java.time.LocalDate;

public final class TestFixtures {

    private TestFixtures() {}

    public static User user(String name, String email) {
        return new User(name, email, "hash");
    }

    public static Account account(String name) {
        return new Account(name);
    }

    public static Membership membershipAs(Account account, User user, Role role) {
        return new Membership(account, user, role);
    }

    public static Project project(String name, Account account) {
        return new Project(name, account);
    }

    public static Todo todo(String title, Project project, User assignee) {
        return new Todo(title, project, assignee, null, false);
    }

    public static Invite pendingInvite(String email, Account account, Role role) {
        return new Invite(email, LocalDate.now().plusDays(7), account, role);
    }
}
