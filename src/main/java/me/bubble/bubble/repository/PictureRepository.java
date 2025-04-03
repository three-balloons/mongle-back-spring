package me.bubble.bubble.repository;

import me.bubble.bubble.domain.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    @Query("""
        SELECT p 
        FROM Picture p 
        WHERE p.file.id = :fileId
    """)
    Optional<Picture> findByFileId(@Param("fileId") Long fileId);

    List<Picture> findByBubbleIdOrderByUpdatedAtAsc(Long bubbleId);
}

