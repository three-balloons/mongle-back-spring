package me.bubble.bubble.config.jwt.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.bubble.bubble.dto.ApiResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter { //JWT 처리 과정에서의 예외, 만료되었거나 유효하지 않은 형식일 경우
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            // JWT 관련 예외 처리 로직을 여기에 작성
            // ApiResponse 객체 생성
            ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                    .code("INAPPROPRIATE_TOKEN")
                    .message("토큰이 부적절합니다.")
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
}
