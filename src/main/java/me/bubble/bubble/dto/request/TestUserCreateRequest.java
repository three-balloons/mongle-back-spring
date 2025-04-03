package me.bubble.bubble.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestUserCreateRequest {
    private String provider;
    private String oauth_id;
    private String name;
}
