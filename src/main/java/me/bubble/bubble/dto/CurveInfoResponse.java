package me.bubble.bubble.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import me.bubble.bubble.domain.Curve;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonAutoDetect
public class CurveInfoResponse {
    private final List<ControlPoint> position;
    private final CurveConfig config;

    public CurveInfoResponse() {
        this.position = new ArrayList<>();
        this.config = new CurveConfig("", 0);
    }

    public CurveInfoResponse(Curve curve) {
        if (curve != null) {
            this.config = new CurveConfig(curve.getColor(), curve.getThickness());

            // curve.getControlPoint()에서 controlPoint 문자열을 가져옴
            String controlPointsStr = curve.getControlPoint();
            this.position = parseControlPoints(controlPointsStr);
        }
        else {
            this.config = new CurveConfig("", 0); // 또는 적절한 기본 값으로 초기화
            this.position = new ArrayList<>(); // 빈 리스트로 초기화
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