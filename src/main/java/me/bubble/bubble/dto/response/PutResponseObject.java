package me.bubble.bubble.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutResponseObject {
    private final Long id;
    private final boolean successYn;

    public PutResponseObject(Long id, boolean successYn) {
        this.id = id;
        this.successYn = successYn;
    }
}
