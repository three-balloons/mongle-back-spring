package me.bubble.bubble.dto.response;

import lombok.Getter;

@Getter
public class AccessTokenResponse {
    private final String accessToken;

    public AccessTokenResponse (String accessToken) {
        this.accessToken = accessToken;
    }
}
