package me.bubble.bubble.repository;

import me.bubble.bubble.domain.User;
import me.bubble.bubble.domain.Workspace;
import org.hibernate.jdbc.Work;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    List<Workspace> findAllByUser(User user);
    Optional<Workspace> findByUserAndName(User user, String name);
}
