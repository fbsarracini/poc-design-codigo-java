package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
public class ListProjectTodosController {

    private final ListProjectTodos listProjectTodos;

    public ListProjectTodosController(ListProjectTodos listProjectTodos) {
        this.listProjectTodos = listProjectTodos;
    }

    @GetMapping("/api/projects/{projectId}/todos")
    @ResponseStatus(HttpStatus.OK)
    public Page<TodoResponse> execute(@PathVariable @Positive Long projectId,
                                      @AuthenticationPrincipal User loggedUser,
                                      @PageableDefault(size = 20) Pageable pageable) {
        return listProjectTodos.execute(projectId, loggedUser, pageable)
                .map(TodoResponse::of);
    }
}
