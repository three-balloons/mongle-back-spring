package me.bubble.bubble.dto.response;

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
    private final Integer top;
    private final Integer left;
    private final Integer width;
    private final Integer height;

    @JsonProperty("isVisible")
    private final boolean visible;

    @JsonProperty("isBubblized")
    private final boolean bubblized;

    private final List<CurveInfoResponse> curves;

    private final List<PostFileResponse> files;
    public BubbleInfoResponse(Bubble bubble, List<CurveInfoResponse> curves, List<PostFileResponse> files) {
        this.name = bubble.getName();
        this.path = bubble.getPath();
        this.top = bubble.getTop();
        this.left = bubble.getLeftmost();
        this.width = bubble.getWidth();
        this.height = bubble.getHeight();
        this.visible = bubble.isVisible();
        this.bubblized = bubble.isBubblized();
        this.curves = curves == null ? Collections.emptyList() : Collections.unmodifiableList(curves);
        this.files = files == null ? Collections.emptyList() : Collections.unmodifiableList(files);
    }
}
