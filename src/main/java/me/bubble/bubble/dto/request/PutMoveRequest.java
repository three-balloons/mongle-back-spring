package me.bubble.bubble.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Integer top;
    private Integer left;
    private Integer height;
    private Integer width;
    @JsonProperty("isBubblized")
    private boolean bubblized;
    @JsonProperty("isVisible")
    private boolean visible;
}
