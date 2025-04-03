package me.bubble.bubble.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.bubble.bubble.domain.Curve;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonAutoDetect
public class CurveInfoResponse implements ShapeResponse{
    private final List<ControlPoint> position;
    private final CurveConfig config;
    private final Long id;
    private final LocalDateTime updatedAt;
    private final String type;

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public CurveInfoResponse() {
        this.position = new ArrayList<>();
        this.config = new CurveConfig("", 0);
        this.id = null;
        this.updatedAt = null;
        this.type = "Curve";
    }

    public CurveInfoResponse(Curve curve) {
        if (curve != null) {
            this.config = new CurveConfig(curve.getColor(), curve.getThickness());

            // curve.getControlPoint()에서 controlPoint 문자열을 가져옴
            String controlPointsStr = curve.getControlPoint();
            this.position = parseControlPoints(controlPointsStr);

            this.id = curve.getId();
            this.updatedAt = curve.getUpdatedAt();
            this.type = "Curve";
        }
        else {
            this.config = new CurveConfig("", 0); // 또는 적절한 기본 값으로 초기화
            this.position = new ArrayList<>(); // 빈 리스트로 초기화
            this.id = null;
            this.updatedAt = null;
            this.type = "Curve";
        }
    }

    private List<ControlPoint> parseControlPoints(String controlPointsStr) {
        List<ControlPoint> controlPoints = new ArrayList<>();

        // 문자열을 6자리씩 잘라서 ControlPoint 객체를 생성하여 리스트에 추가
        for (int i = 0; i < controlPointsStr.length(); i += 6) {
            String controlPointStr = controlPointsStr.substring(i, i + 6);
            ControlPoint controlPoint = new ControlPoint(controlPointStr);
            controlPoints.add(controlPoint);
        }

        return controlPoints;
    }
}