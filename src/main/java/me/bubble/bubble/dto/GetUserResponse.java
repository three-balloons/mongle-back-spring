package me.bubble.bubble.dto;

import lombok.Builder;
import lombok.Getter;
import me.bubble.bubble.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class GetUserResponse {
    private final String oauth_id;
    private final String provider;
    private final String name;
    private final LocalDateTime deleted_at;
    private final String email;
    private final String refresh_token;

    public GetUserResponse(User user) {
        this.oauth_id = user.getOauthId();
        this.provider = user.getProvider();
        this.name = user.getName();
        this.deleted_at = user.getDeletedAt();
        this.email = user.getEmail();
        this.refresh_token = user.getRefreshToken();
    }
}
