package me.bubble.bubble.repository;

import me.bubble.bubble.domain.Bubble;
import me.bubble.bubble.domain.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BubbleRepository extends JpaRepository<Bubble, Long> {

    List<Bubble> findByWorkspaceIdAndPathStartsWithAndPathDepthLessThanEqualOrderByPathDepthAsc(UUID workspaceId, String path, int pathDepth);
    void deleteByPathStartingWithAndWorkspaceId(String path, UUID workspaceId);
    Optional<Bubble> findTopByWorkspaceIdOrderByPathDepthDesc(UUID workspaceId);
    Optional<Bubble> findByPathAndWorkspaceId(String path, UUID workspaceId);
    List<Bubble> findByPathDepthAndPathStartingWithAndWorkspaceId(int pathDepth, String path, UUID workspaceId);
    List<Bubble> findByPathDepthAndWorkspaceId(int pathDepth, UUID workspaceId);
    List<Bubble> findByWorkspaceAndPathStartingWith(Workspace workspace, String path);
}
