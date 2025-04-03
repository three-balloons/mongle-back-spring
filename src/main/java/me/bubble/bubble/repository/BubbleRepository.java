package me.bubble.bubble.repository;

import me.bubble.bubble.domain.Bubble;
import me.bubble.bubble.domain.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BubbleRepository extends JpaRepository<Bubble, Long> {

    @Modifying
    @Query("DELETE FROM Bubble b WHERE b.workspace.id = :workspaceId AND " +
            "(b.path = :path OR b.path LIKE CONCAT(:path, '/%'))")
    void deleteByPathAndSubPaths(@Param("path") String path, @Param("workspaceId") UUID workspaceId);

    Optional<Bubble> findByPathAndWorkspaceId(String path, UUID workspaceId);

    @Query("SELECT b FROM Bubble b WHERE b.workspace = :workspace AND " +
            "(b.path = :path OR b.path LIKE CONCAT(:path, '/%'))")
    List<Bubble> findByWorkspaceAndExactPathOrSubPaths(@Param("workspace") Workspace workspace,
                                                       @Param("path") String path);

    @Query("SELECT b FROM Bubble b WHERE b.workspace.id = :workspaceId AND " +
            "(b.path = :path OR b.path LIKE CASE WHEN :path = '/' THEN '/%' ELSE CONCAT(:path, '/%') END) " +
            "AND b.pathDepth <= :pathDepth " +
            "ORDER BY b.pathDepth ASC")
    List<Bubble> findByWorkspaceIdAndPathWithExactStart(@Param("workspaceId") UUID workspaceId, @Param("path") String path, @Param("pathDepth") int pathDepth);


//    List<Bubble> findByPathDepthAndPathStartingWithAndWorkspaceId(int pathDepth, String path, UUID workspaceId);
//    List<Bubble> findByWorkspaceIdAndPathStartsWithAndPathDepthLessThanEqualOrderByPathDepthAsc(UUID workspaceId, String path, int pathDepth);
//    List<Bubble> findByWorkspaceIdAndPathStartsWithOrderByPathDepthAsc(UUID workspaceId, String path);
//    List<Bubble> findByPathDepthAndWorkspaceId(int pathDepth, UUID workspaceId);
//    Optional<Bubble> findTopByWorkspaceIdOrderByPathDepthDesc(UUID workspaceId);
}
