package me.bubble.bubble.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class BubbleTreeResponse {
    private String name;
    private List<BubbleTreeResponse> children;

    public BubbleTreeResponse (String name, List<BubbleTreeResponse> children) {
        this.name = name;
        this.children = children == null ? Collections.emptyList() : Collections.unmodifiableList(children);
    }
}
