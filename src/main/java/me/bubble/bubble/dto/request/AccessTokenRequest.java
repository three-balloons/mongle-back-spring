package me.bubble.bubble.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessTokenRequest {
    private String provider;
    private String code;
    private String redirect_uri;
}
