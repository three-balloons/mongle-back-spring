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
    private int top;
    private int left;
    private int height;
    private int width;
    private boolean bubblized;
    private boolean visible;
}
