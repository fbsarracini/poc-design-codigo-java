package io.github.fbsarracini.javadesign.account;

public enum Role {
    OWNER, ADMIN, MEMBER, CLIENT;

    public boolean canInvite() {
        return this == OWNER || this == ADMIN;
    }

    public boolean canCreateProject() {
        return this != CLIENT;
    }

    public boolean isAdminOrAbove() {
        return this == OWNER || this == ADMIN;
    }
}
