package io.github.fbsarracini.javadesign.project;

public record ProjectSummaryResponse(Long id, String name, ProjectStatus status) {

    public static ProjectSummaryResponse of(Project project) {
        return new ProjectSummaryResponse(
                project.getId(),
                project.getName(),
                project.getStatus()
        );
    }
}
