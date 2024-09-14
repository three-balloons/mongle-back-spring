package me.bubble.bubble.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.User;
import me.bubble.bubble.exception.UserNotFoundException;
import me.bubble.bubble.repository.UserRepository;
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

    public User findUserByOauthId(String oauthId) {
        return userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    public User updateUserNameAndEmail(String oAuthId, String newName, String newEmail) {
        // 사용자 조회
        User user = userRepository.findByOauthId(oAuthId)
                .orElseThrow(() -> new UserNotFoundException("User not found " + oAuthId));

        // 이름과 이메일 업데이트
        user.setName(newName);
        user.setEmail(newEmail);

        // 변경된 사용자 정보 저장
        return userRepository.save(user);
    }

    @Transactional
    public void updateDeletedAtByOauthId(String oauthId) {
        // OAuthId로 User를 조회
        User user = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // deletedAt 필드를 현재 시간으로 업데이트
        user.setDeletedAt(LocalDateTime.now());

        // 변경 사항 저장
        userRepository.save(user);
    }
}
