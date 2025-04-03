package me.bubble.bubble.dto.response;

import lombok.Getter;
import lombok.Setter;
import me.bubble.bubble.domain.File;
import me.bubble.bubble.domain.Picture;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class PictureResponse implements ShapeResponse {
    private Long id;
    private Integer top;
    private Integer left;
    private Integer height;
    private Integer width;
    private Boolean isFlippedX;
    private Boolean isFlippedY;
    private Integer angle;
    private Long bubbleId;
    private Long fid;
    private LocalDateTime updatedAt;
    private String type;

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public PictureResponse(Picture picture) {
        this.id = picture.getId();
        this.bubbleId = picture.getBubble().getId();  // 파일 경로
        this.fid = picture.getId();
        this.top = picture.getTop();
        this.left = picture.getLeftmost();
        this.height = picture.getHeight();
        this.width = picture.getWidth();
        this.isFlippedX = picture.isFlippedX();
        this.isFlippedY = picture.isFlippedY();
        this.angle = picture.getAngle();
        this.updatedAt = picture.getUpdatedAt();
        this.type = "Picture";
    }
}
