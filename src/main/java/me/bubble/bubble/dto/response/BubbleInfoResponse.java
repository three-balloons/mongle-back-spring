package me.bubble.bubble.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import me.bubble.bubble.domain.Bubble;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Getter
@JsonPropertyOrder({ "path", "top", "left", "width", "height", "isVisible", "isBubblized", "curves", "children" })
public class BubbleInfoResponse {
    private final String name;
    private final String path;
    private final Integer top;
    private final Integer left;
    private final Integer width;
    private final Integer height;

    @JsonProperty("isVisible")
    private final boolean visible;

    @JsonProperty("isBubblized")
    private final boolean bubblized;

    private final List<ShapeResponse> shapes;
    public BubbleInfoResponse(Bubble bubble, List<CurveInfoResponse> curves, List<PictureResponse> files) {
        this.name = bubble.getName();
        this.path = bubble.getPath();
        this.top = bubble.getTop();
        this.left = bubble.getLeftmost();
        this.width = bubble.getWidth();
        this.height = bubble.getHeight();
        this.visible = bubble.isVisible();
        this.bubblized = bubble.isBubblized();

        List<ShapeResponse> mergedList = new ArrayList<>();
        if (curves != null) mergedList.addAll(curves);
        if (files != null) mergedList.addAll(files);

        // updatedAt 기준 오름차순 정렬 (먼저 업데이트된 게 먼저)
        mergedList.sort(Comparator.comparing(ShapeResponse::getUpdatedAt));

        // 변경 불가능한 리스트로 저장
        this.shapes = Collections.unmodifiableList(mergedList);
    }
}
