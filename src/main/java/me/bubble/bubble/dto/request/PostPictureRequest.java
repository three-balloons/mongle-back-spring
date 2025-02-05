package me.bubble.bubble.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostPictureRequest {
    private Integer top;
    private Integer left;
    private Integer height;
    private Integer width;
    private Boolean isFlippedX;
    private Boolean isFlippedY;
    private Integer angle;
    private String path;
    private Long fid;
}
