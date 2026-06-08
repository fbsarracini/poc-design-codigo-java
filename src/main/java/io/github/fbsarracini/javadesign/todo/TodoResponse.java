package io.github.fbsarracini.javadesign.todo;

public record TodoResponse(Long id, String title, boolean completed, boolean visibleToClient) {

    public static TodoResponse of(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.isCompleted(), todo.isVisibleToClient());
    }
}
