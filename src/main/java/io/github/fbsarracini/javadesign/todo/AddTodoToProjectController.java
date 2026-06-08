package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
public class AddTodoToProjectController {

    private final AddTodoToProject addTodoToProject;

    public AddTodoToProjectController(AddTodoToProject addTodoToProject) {
        this.addTodoToProject = addTodoToProject;
    }

    @PostMapping("/api/projects/{projectId}/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public NewTodoResponse execute(@PathVariable @Positive Long projectId,
                                   @RequestBody @Valid NewTodoRequest request,
                                   @AuthenticationPrincipal User loggedUser) {
        Todo todo = addTodoToProject.execute(projectId, loggedUser, request);
        return NewTodoResponse.of(todo);
    }
}
