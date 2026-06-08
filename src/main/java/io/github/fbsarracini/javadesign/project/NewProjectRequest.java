package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.account.Account;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record NewProjectRequest(
        @NotBlank String name
) implements @Valid NewProjectData {

    @Override
    public Project toNewProject(Account account) {
        return new Project(name, account);
    }
}
