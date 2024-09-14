package me.bubble.bubble.service;

import lombok.RequiredArgsConstructor;
import me.bubble.bubble.dto.OAuthResponseDto;
import me.bubble.bubble.exception.NoSubException;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final WebClient.Builder webClientBuilder;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    Base64.Decoder decoder = Base64.getDecoder();
    JsonParser jsonParser = new BasicJsonParser();

    @Transactional
    public String[] getKakaoOAuthId(String code, String redirectUri) {
        WebClient webClient = webClientBuilder.build();
        Mono<OAuthResponseDto> kakaoResponseDtoMono = webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .bodyValue("grant_type=authorization_code&client_id="+kakaoClientId+"&redirect_uri="+redirectUri+"&code=" + code+"&client_secret="+kakaoClientSecret)
                .retrieve()
                .bodyToMono(OAuthResponseDto.class);
        //요청을 받아서, idToken을 decode 후에 sub을 가져온다.sub: 유저 식별자
        String idToken = kakaoResponseDtoMono.block().getId_token();
        return getInfoFromIdToken(idToken, "Kakao");
    }

    @Transactional
    public String[] getGoogleOAuthId(String code, String redirectUri) {
        WebClient webClient = webClientBuilder.build();
        Mono<OAuthResponseDto> googleResponseDtoMono = webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .bodyValue("code=" + code + "&client_id=" + googleClientId + "&client_secret=" + googleClientSecret + "&redirect_uri=" + redirectUri + "&grant_type=authorization_code")
                .retrieve()
                .bodyToMono(OAuthResponseDto.class);

        String idToken = googleResponseDtoMono.block().getId_token();
        return getInfoFromIdToken(idToken, "Google");

    }
    private String[] getInfoFromIdToken(String idToken, String provider) {
        final String payloadJwt = idToken.split("\\.")[1];
        byte[] payload = decoder.decode(payloadJwt);
        String decodedPayload = new String(payload);

        Map<String, Object> jsonArray = jsonParser.parseMap(decodedPayload);

        if (!jsonArray.containsKey("sub")) {
            throw new NoSubException("Sub is not included");
        }
        String sub = jsonArray.get("sub").toString();
        // email 필드가 없을 경우 빈 문자열로 처리
        String email = jsonArray.containsKey("email") ? jsonArray.get("email").toString() : "";
        String name = "";

        // name 필드가 없을 경우 빈 문자열로 처리
        if (provider.equals("Google")) {
            name = jsonArray.containsKey("name") ? jsonArray.get("name").toString() : "";
        } else if (provider.equals("Kakao")) {
            name = jsonArray.containsKey("nickname") ? jsonArray.get("nickname").toString() : "";
        }
        // 배열로 변환

        String[] result = new String[3];
        result[0] = sub;
        result[1] = email;
        result[2] = name;

        return result;
    }
}
