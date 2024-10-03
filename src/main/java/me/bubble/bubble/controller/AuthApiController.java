package me.bubble.bubble.controller;

import lombok.RequiredArgsConstructor;
import me.bubble.bubble.config.jwt.JwtTokenProvider;
import me.bubble.bubble.dto.request.AccessTokenRequest;
import me.bubble.bubble.dto.response.AccessTokenResponse;
import me.bubble.bubble.dto.response.ApiResponse;
import me.bubble.bubble.service.AuthService;
import me.bubble.bubble.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthApiController {
    private final AuthService authService;

    @PostMapping("/api/auth/access")
    public ApiResponse<AccessTokenResponse> getAccessToken(@RequestBody AccessTokenRequest request) {
        AccessTokenResponse accessTokenResponse = authService.getAccessTokenResponse(request);
        return ApiResponse.<AccessTokenResponse>builder()
                .code("OK")
                .message("")
                .data(accessTokenResponse)
                .build();
    }


    @PostMapping("/api/auth/test")
    public ApiResponse<AccessTokenResponse> getTestAccessToken(@RequestBody AccessTokenRequest request) {
        AccessTokenResponse accessTokenResponse = authService.getTestAccessTokenResponse(request);
        return ApiResponse.<AccessTokenResponse>builder()
                .code("OK")
                .message("")
                .data(accessTokenResponse)
                .build();
    }
}
