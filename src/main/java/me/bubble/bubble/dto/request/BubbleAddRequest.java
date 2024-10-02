package me.bubble.bubble.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("isVisible")
    private boolean visible;
    @JsonProperty("isBubblized")
    private boolean bubblized;
}
