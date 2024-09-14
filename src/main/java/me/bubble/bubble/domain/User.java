package me.bubble.bubble.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Setter
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "provider")
    private String provider;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Builder
    public User(String oauthId, String provider, String email, String name, LocalDateTime deletedAt,
                String refreshToken) {
        this.oauthId = oauthId;
        this.provider = provider;
        this.refreshToken = refreshToken;
        this.email = email;
        this.name = name;
        this.deletedAt = deletedAt;
    }

}
