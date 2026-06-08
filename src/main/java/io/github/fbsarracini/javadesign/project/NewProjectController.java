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
public class NewProjectController {

    private final CreateNewProject createNewProject;

    public NewProjectController(CreateNewProject createNewProject) {
        this.createNewProject = createNewProject;
    }

    @PostMapping("/api/accounts/{accountId}/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public NewProjectResponse execute(@PathVariable @Positive Long accountId,
                                      @RequestBody @Valid NewProjectRequest request,
                                      @AuthenticationPrincipal User loggedUser) {
        Project project = createNewProject.execute(accountId, loggedUser, request);
        return NewProjectResponse.of(project);
    }
}
