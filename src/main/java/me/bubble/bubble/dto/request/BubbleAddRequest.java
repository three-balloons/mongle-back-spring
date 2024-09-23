package me.bubble.bubble.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BubbleAddRequest {
    private String name;
    private Integer top;
    private Integer left;
    private Integer height;
    private Integer width;
    private boolean isVisible;
    private boolean isBubblized;
}
