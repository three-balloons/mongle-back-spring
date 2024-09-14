package me.bubble.bubble.dto;

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
    private int top;
    private int left;
    private int height;
    private int width;
    private boolean isVisible;
    private boolean isBubblized;
}
