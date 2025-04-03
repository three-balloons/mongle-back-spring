package me.bubble.bubble.dto.response;

import lombok.Getter;
import me.bubble.bubble.domain.User;

import java.time.LocalDateTime;

@Getter
public class UserResponse {
    private final String oauth_id;
    private final String provider;
    private final String name;
    private final LocalDateTime deleted_at;
    private final String email;
    private final String refresh_token;

    public UserResponse(User user) {
        this.oauth_id = user.getOauthId();
        this.provider = user.getProvider();
        this.name = user.getName();
        this.deleted_at = user.getDeletedAt();
        this.email = user.getEmail();
        this.refresh_token = user.getRefreshToken();
    }
}
