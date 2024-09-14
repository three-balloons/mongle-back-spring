package me.bubble.bubble.config.jwt.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.bubble.bubble.dto.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint { // 인증되지 않은 사용자가 보호된 페이지 접근
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // ApiResponse 객체 생성
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code("NEED_AUTHENTICATION")
                .message("인증이 필요합니다.")
                .data(null)
                .build();

        // HTTP 상태 코드를 200으로 설정
        response.setStatus(HttpServletResponse.SC_OK);

        // 응답의 Content-Type을 JSON으로 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // ApiResponse 객체를 JSON으로 변환하여 응답에 쓰기
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
