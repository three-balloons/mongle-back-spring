package me.bubble.bubble.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponse<T> { // 데이터 객체 하나 넘겨주는 ApiResponse
    // 얘로 ApiResponse 통일
    private final String code;
    private final String message;
    private final T data;

    @Builder
    public ApiResponse(String message, String code, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
