package me.bubble.bubble.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TempFileResponse {
    private final Long fileId;

    public TempFileResponse(Long fileId) {
        this.fileId = fileId;
    }
}
