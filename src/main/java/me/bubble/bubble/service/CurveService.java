package me.bubble.bubble.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.Bubble;
import me.bubble.bubble.domain.Curve;
import me.bubble.bubble.exception.CurveNotFoundException;
import me.bubble.bubble.repository.CurveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CurveService {
    private final CurveRepository curveRepository;

    public List<Curve> findCurvesByBubble(Bubble bubble) {
        return curveRepository.findByBubbleId(bubble.getId());
    }

    @Transactional
    public void deleteCurveById(Long id) {
        Curve curve = curveRepository.findById(id)
                .orElseThrow(() -> new CurveNotFoundException("Curve with id " + id + " not found"));

        curveRepository.delete(curve);
    }

    public Curve findCurveById (Long id) {
        Curve curve = curveRepository.findById(id)
                .orElseThrow(() -> new CurveNotFoundException("Curve with id " + id + " not found"));

        return curve;
    }

    @Transactional
    public Curve updateCurve(Long id, String color,
                             int thickness, Bubble bubble, String controlPoint) {
        Curve curve = curveRepository.findById(id)
                .orElseThrow(() -> new CurveNotFoundException("Curve not found"));
        curve.update(color, thickness, bubble, controlPoint);
        return curveRepository.save(curve);
    }

    @Transactional
    public Curve saveCurve(Curve curve) {
        return curveRepository.save(curve);
    }
}
