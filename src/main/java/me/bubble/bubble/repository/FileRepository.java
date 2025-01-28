package me.bubble.bubble.repository;

import me.bubble.bubble.domain.Curve;
import me.bubble.bubble.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository  extends JpaRepository<File, Long> {
    List<File> findByBubbleId(Long bubbleId);
}
