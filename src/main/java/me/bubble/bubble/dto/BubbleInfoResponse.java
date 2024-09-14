package me.bubble.bubble.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import me.bubble.bubble.domain.Bubble;

import java.util.Collections;
import java.util.List;

@Getter
@JsonPropertyOrder({ "path", "top", "left", "width", "height", "isVisible", "isBubblized", "curves", "children" })
public class BubbleInfoResponse {
    private final String name;
    private final String path;
    private final int top;
    private final int left;
    private final int width;
    private final int height;

    @JsonProperty("isVisible")
    private final boolean visible;

    @JsonProperty("isBubblized")
    private final boolean bubblized;

    private final List<CurveInfoResponse> curves;
    public BubbleInfoResponse(Bubble bubble, List<CurveInfoResponse> curves) {
        this.name = bubble.getName();
        this.path = bubble.getPath();
        this.top = bubble.getTop();
        this.left = bubble.getLeftmost();
        this.width = bubble.getWidth();
        this.height = bubble.getHeight();
        this.visible = bubble.isVisible();
        this.bubblized = bubble.isBubblized();
        this.curves = curves == null ? Collections.emptyList() : Collections.unmodifiableList(curves);
    }
}
