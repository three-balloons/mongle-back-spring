package me.bubble.bubble.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.Bubble;
import me.bubble.bubble.domain.Curve;
import me.bubble.bubble.domain.Workspace;
import me.bubble.bubble.exception.BubbleNotFoundException;
import me.bubble.bubble.exception.NoBubbleInWorkspaceException;
import me.bubble.bubble.repository.BubbleRepository;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BubbleService {
    private final BubbleRepository bubbleRepository;

    public Bubble findByPathAndWorkspaceId(String path, UUID workspaceId) {
        return bubbleRepository.findByPathAndWorkspaceId(path, workspaceId)
                .orElseThrow(() -> new BubbleNotFoundException("Bubble Not Found"));
    }

    public List<Bubble> findChildrenByBubbleAndWorkspaceId(Bubble bubble, UUID workspaceId) {
        return bubbleRepository.findByPathDepthAndPathStartingWithAndWorkspaceId(bubble.getPathDepth() + 1,
                bubble.getPath(), workspaceId);
    }

    public int getMaxPathDepth(UUID workspaceId) {
        Bubble bubble = bubbleRepository.findTopByWorkspaceIdOrderByPathDepthDesc(workspaceId)
                .orElseThrow(() -> new NoBubbleInWorkspaceException("No Bubble In Workspace " + workspaceId));
        return bubble.getPathDepth();
    }

    public List<Bubble> findBubblesByPathDepthAndWorkspaceId(int pathDepth, UUID workspaceId) {
        return bubbleRepository.findByPathDepthAndWorkspaceId(pathDepth, workspaceId);
    }

    @Transactional
    public void deleteByPathStartingWithAndWorkspaceId(String path, UUID workspaceId) {
        bubbleRepository.deleteByPathStartingWithAndWorkspaceId(path, workspaceId);
    }

    @Transactional
    public Bubble saveBubble(Bubble bubble) {
        return bubbleRepository.save(bubble);
    }

    public List<Bubble> getBubblesByWorkspaceAndPathAndPathDepth(UUID workspaceId, String path, int pathDepth) {
        return bubbleRepository.findByWorkspaceIdAndPathStartsWithAndPathDepthLessThanEqualOrderByPathDepthAsc(workspaceId, path, pathDepth);
    }

    @Transactional
    public Bubble updateBubble(Long id, String name, int top, int leftmost, int width, int height,
                               String path, int pathDepth, boolean bubblized, boolean visible, Workspace workspace) {
        Bubble bubble = bubbleRepository.findById(id)
                .orElseThrow(() -> new BubbleNotFoundException("Bubble not found"));
        bubble.update(name, top, leftmost, width, height, path, pathDepth, bubblized, visible, workspace);
        return bubbleRepository.save(bubble);
    }

    @Transactional
    public void updateBubblePaths(Workspace workspace, String oldPath, String newPath) {
        int oldPathSlashCount = countOccurrences(oldPath, '/');
        //oldPath로 시작하는 모든 버블들, 즉 해당 버블과 그 자녀들을 가져온다.
        List<Bubble> bubbles = bubbleRepository.findByWorkspaceAndPathStartingWith(workspace, oldPath);
        for (Bubble tempBubble : bubbles) {
            int newPathSlashCount = countOccurrences(newPath, '/');
            int slashCountDifference = newPathSlashCount - oldPathSlashCount;

            String currentPath = tempBubble.getPath();
            // path 부분을 newPath로 변경
            String updatedPath = currentPath.replaceFirst(oldPath, newPath);
            tempBubble.setPath(updatedPath);
            tempBubble.setPathDepth(tempBubble.getPathDepth() + slashCountDifference);
        }

        bubbleRepository.saveAll(bubbles);
    }
    private int countOccurrences (String str,char character){
        return (int) str.chars().filter(ch -> ch == character).count();
    }
}