package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class ListAccountProjectsController {

    private final ListAccountProjects listAccountProjects;

    public ListAccountProjectsController(ListAccountProjects listAccountProjects) {
        this.listAccountProjects = listAccountProjects;
    }

    @GetMapping("/api/accounts/{accountId}/projects")
    @ResponseStatus(HttpStatus.OK)
    public List<ProjectSummaryResponse> execute(@PathVariable @Positive Long accountId,
                                                @AuthenticationPrincipal User loggedUser) {
        return listAccountProjects.execute(accountId, loggedUser);
    }
}
