package io.github.fbsarracini.javadesign.project;

public record NewProjectResponse(Long id) {

    public static NewProjectResponse of(Project project) {
        return new NewProjectResponse(project.getId());
    }
}
