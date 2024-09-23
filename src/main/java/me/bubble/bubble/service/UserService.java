package me.bubble.bubble.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.User;
import me.bubble.bubble.dto.request.PutUserRequest;
import me.bubble.bubble.dto.response.UserResponse;
import me.bubble.bubble.exception.UserNotFoundException;
import me.bubble.bubble.repository.UserRepository;
import me.bubble.bubble.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User createUser(String oauthId, String provider, String email, String name, LocalDateTime deletedAt, String refreshToken) {
        User user = User.builder()
                .oauthId(oauthId)
                .provider(provider)
                .email(email)
                .name(name)
                .deletedAt(deletedAt)
                .refreshToken(refreshToken)
                .build();

        return userRepository.save(user);  // DB에 저장 후 저장된 객체 반환
    }
    public User findUserEntityByOAuthId(String oAuthId) {
        return userRepository.findByOauthId(oAuthId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserResponse findUserByOauthId() {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        User user = userRepository.findByOauthId(oAuthId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return new UserResponse(user);
    }

    @Transactional
    public UserResponse updateUserNameAndEmail(PutUserRequest request) {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        // 사용자 조회
        User user = userRepository.findByOauthId(oAuthId)
                .orElseThrow(() -> new UserNotFoundException("User not found " + oAuthId));

        // 이름과 이메일 업데이트
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // 변경된 사용자 정보 저장
        return new UserResponse(userRepository.save(user));
    }

    @Transactional
    public void updateDeletedAtByOauthId() {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        // OAuthId로 User를 조회
        User user = userRepository.findByOauthId(oAuthId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // deletedAt 필드를 현재 시간으로 업데이트
        user.setDeletedAt(LocalDateTime.now());

        // 변경 사항 저장
        userRepository.save(user);
    }
}
