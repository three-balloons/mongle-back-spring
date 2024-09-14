package me.bubble.bubble.util;

import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.User;
import me.bubble.bubble.exception.UserNotAuthenticatedException;
import me.bubble.bubble.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    // 현재 인증된 사용자 객체 반환 (OAuthId로 찾아서)
    public static String getCurrentUserOAuthId() {
        // SecurityContext에서 Authentication 객체를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Authentication 객체에서 principal 가져오기
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            // principal이 UserDetails 타입인지 확인
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;
                return user.getUsername();  // oauthId로 설정된 값
            }
        }

        // 인증되지 않은 경우 예외 처리 또는 null 반환 (예시에서는 예외 처리)
        throw new UserNotAuthenticatedException("User Authentication Failed");
    }
}
