package me.bubble.bubble.dto.response;

import lombok.Getter;
import lombok.Setter;
import me.bubble.bubble.domain.File;

@Getter
@Setter
public class PostFileResponse {
    private Integer top;
    private Integer left;
    private Integer height;
    private Integer width;
    private Boolean isFlippedX;
    private Boolean isFlippedY;
    private Integer angle;
    private String path;
    private Long fid;

    // File 객체를 받아서 필드에 값을 설정하는 생성자
    public PostFileResponse(File file, String bubblePath) {
        this.path = bubblePath;  // 파일 경로
        this.fid = file.getId();
        this.top = file.getTop();
        this.left = file.getLeftmost();
        this.height = file.getHeight();
        this.width = file.getWidth();
        this.isFlippedX = file.isFlippedX();
        this.isFlippedY = file.isFlippedY();
        this.angle = file.getAngle();
    }
}
