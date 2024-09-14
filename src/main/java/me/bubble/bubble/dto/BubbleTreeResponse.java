package me.bubble.bubble.dto;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class BubbleTreeResponse {
    private final String name;
    private final List<BubbleTreeResponse> children;

    public BubbleTreeResponse (String name, List<BubbleTreeResponse> children) {
        this.name = name;
        this.children = children == null ? Collections.emptyList() : Collections.unmodifiableList(children);
    }
}
