package me.bubble.bubble.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.repository.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
@Service
public class JwtTokenProvider {
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    private final long JWT_EXPIRATION = 86400000L; // 1일

    public String generateToken(String oauthId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setIssuer(jwtProperties.getIssuer())
                .claim("oauthId", oauthId)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey())
                .compact();
    }

    // JWT 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // JWT 토큰에서 인증 정보 추출
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();

        String oauthId = claims.get("oauthId", String.class);
        // OAuthId가 유효한지 확인하는 추가 로직
        // deletedAt으로 체크하는 걸 추가
        me.bubble.bubble.domain.User user = userRepository.findByOauthId(oauthId) // 우리가 만든 User 객체
                .orElseThrow(() -> new UsernameNotFoundException("Invalid OAuth ID: " + oauthId));
        if (user.getDeletedAt() != null) { // 삭제된 경우 예외를 던진다.
            throw new DisabledException("Deleted User");
        }

        User principal = new User(oauthId, "", Collections.emptyList()); // 이 때의 User는 스프링 시큐리티에서의 User
        // 인증 정보를 반환할 때 사용되는 유저 객체는 스프링 시큐리티에서 사용되는 유저여야 함. (principal로 사용되는 유저 객체는 UserDetails 인터페이스 구현해야 함.)
        return new UsernamePasswordAuthenticationToken(principal, token, Collections.emptyList());
    }

    // 요청에서 JWT 토큰 추출
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7)); // Remove "Bearer " prefix
        }
        return Optional.empty();
    }
}

