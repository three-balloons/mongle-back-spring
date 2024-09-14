package me.bubble.bubble.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthResponseDto {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private Long expires_in;
    private String scope;
    private String id_token;
}
