package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, Long> {
    boolean existsByProjectAndUser(Project project, User user);
}
