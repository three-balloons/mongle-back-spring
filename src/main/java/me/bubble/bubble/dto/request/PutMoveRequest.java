package me.bubble.bubble.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PutMoveRequest {
    private String newPath;
    private String name;
    private Integer top;
    private Integer left;
    private Integer height;
    private Integer width;
    private boolean bubblized;
    private boolean visible;
}
