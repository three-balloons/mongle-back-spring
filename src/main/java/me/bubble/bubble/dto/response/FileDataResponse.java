package me.bubble.bubble.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDataResponse {
    private final byte[] fileData;

    public FileDataResponse(byte[] fileData) {
        this.fileData = fileData;
    }
}
