package me.bubble.bubble.dto.response;

import lombok.Getter;

@Getter
public class CurveConfig {
    private final String color;
    private final int thickness;

    public CurveConfig(String color, int thickness) {
        this.color = color;
        this.thickness = thickness;
    }
}