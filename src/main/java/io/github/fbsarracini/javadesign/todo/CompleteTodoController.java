package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
public class CompleteTodoController {

    private final CompleteTodo completeTodo;

    public CompleteTodoController(CompleteTodo completeTodo) {
        this.completeTodo = completeTodo;
    }

    @PostMapping("/api/todos/{todoId}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@PathVariable @Positive Long todoId,
                        @AuthenticationPrincipal User loggedUser) {
        completeTodo.execute(todoId, loggedUser);
    }
}
