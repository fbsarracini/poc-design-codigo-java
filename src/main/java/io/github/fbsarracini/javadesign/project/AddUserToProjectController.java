package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
public class AddUserToProjectController {

    private final AddUserToProject addUserToProject;

    public AddUserToProjectController(AddUserToProject addUserToProject) {
        this.addUserToProject = addUserToProject;
    }

    @PostMapping("/api/projects/{projectId}/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@PathVariable @Positive Long projectId,
                        @RequestBody @Valid AddUserToProjectRequest request,
                        @AuthenticationPrincipal User loggedUser) {
        addUserToProject.execute(projectId, loggedUser, request.email());
    }
}
