package me.bubble.bubble.repository;

import me.bubble.bubble.domain.Bubble;
import me.bubble.bubble.domain.Curve;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurveRepository extends JpaRepository<Curve, Long> {
    List<Curve> findByBubbleId(Long bubbleId);
    Optional<Curve> findById(Long id);
}
