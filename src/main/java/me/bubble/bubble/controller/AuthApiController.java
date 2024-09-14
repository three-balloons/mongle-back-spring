package me.bubble.bubble.controller;

import lombok.RequiredArgsConstructor;
import me.bubble.bubble.config.jwt.JwtTokenProvider;
import me.bubble.bubble.domain.User;
import me.bubble.bubble.dto.*;
import me.bubble.bubble.exception.InappropriatePayloadException;
import me.bubble.bubble.exception.InappropriateProviderException;
import me.bubble.bubble.exception.UserNotFoundException;
import me.bubble.bubble.service.AuthService;
import me.bubble.bubble.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthApiController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/api/auth/access")
    public ApiResponse<AccessTokenResponse> getAccessToken(@RequestBody AccessTokenRequest request) {
        if (request.getProvider().equals("KAKAO")) {
            String[] info = authService.getKakaoOAuthId(request.getCode(), request.getRedirect_uri());
            String accessToken = CheckAndSaveUserAndReturnToken(request.getProvider(), info);
            AccessTokenResponse accessTokenResponse = new AccessTokenResponse(accessToken);

            return ApiResponse.<AccessTokenResponse>builder()
                    .code("OK")
                    .message("")
                    .data(accessTokenResponse)
                    .build();

        } else if (request.getProvider().equals("GOOGLE")) {
            String[] info = authService.getGoogleOAuthId(request.getCode(), request.getRedirect_uri());

            String accessToken = CheckAndSaveUserAndReturnToken(request.getProvider(), info);
            AccessTokenResponse accessTokenResponse = new AccessTokenResponse(accessToken);
            return ApiResponse.<AccessTokenResponse>builder()
                    .code("OK")
                    .message("")
                    .data(accessTokenResponse)
                    .build();
        }
        throw new InappropriateProviderException("Inapproprate provider");
    }

    private String CheckAndSaveUserAndReturnToken (String provider, String[] info) {
        String oAuthId = info[0];
        String email = info[1];
        String name = info[2];
        try { // 해당 유저가 존재할 때
            User user = userService.findUserByOauthId(oAuthId);
            if (user.getDeletedAt() == null) { // 삭제되지 않은 경우 토큰 새로 생성 후 리턴
                return jwtTokenProvider.generateToken(oAuthId);
            } else {
                throw new InappropriatePayloadException("Inappropriate Payload");
            }
        } catch (UserNotFoundException ex) {
            User createdUser = userService.createUser(oAuthId, provider, email, name, null, null);
            return jwtTokenProvider.generateToken(oAuthId);
        }
    }
}
