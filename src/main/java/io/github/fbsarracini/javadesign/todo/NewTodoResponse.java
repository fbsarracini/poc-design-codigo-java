package io.github.fbsarracini.javadesign.todo;

public record NewTodoResponse(Long id) {

    public static NewTodoResponse of(Todo todo) {
        return new NewTodoResponse(todo.getId());
    }
}
