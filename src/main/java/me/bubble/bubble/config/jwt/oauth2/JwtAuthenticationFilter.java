package me.bubble.bubble.config.jwt.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.config.jwt.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
       //헤더에서 Jwt Token 받기
        String token = jwtTokenProvider.extractAccessToken(request).orElse(null);
        //유효한 토큰인지?
        if (token != null && jwtTokenProvider.validateToken(token)) {
            //토큰이 유효하면 토큰으로부터 유저 정보 받기
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            //SecurityContext에 Authentication 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

       filterChain.doFilter(request, response);
    }
}
