package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    Page<Todo> findByProject(Project project, Pageable pageable);
    Page<Todo> findByProjectAndVisibleToClient(Project project, boolean visibleToClient, Pageable pageable);
}
