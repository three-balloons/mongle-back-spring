package me.bubble.bubble.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.User;
import me.bubble.bubble.domain.Workspace;
import me.bubble.bubble.exception.WorkspaceNotFoundException;
import me.bubble.bubble.repository.WorkspaceRepository;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;

    public Workspace findWorkspaceById(UUID id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace Not Found " + id));
    }

    public List<Workspace> getAllWorkspacesByUser(User user) {
        return workspaceRepository.findAllByUserOrderByUpdatedAtDesc(user);
    }

    @Transactional
    public Workspace updateNameAndTheme(Workspace workspace, String name, String theme) {
        // deletedAt 필드를 현재 시간으로 업데이트
        workspace.setName(name);
        workspace.setTheme(theme);

        // 변경 사항 저장
        return workspaceRepository.save(workspace);
    }

    @Transactional
    public Workspace createWorkspace(String name, String theme, User user) {

        // Workspace 생성
        Workspace workspace = Workspace.builder()
                .name(name)
                .theme(theme)
                .deletedAt(null)
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();

        // 저장
        return workspaceRepository.save(workspace);
    }

    public void updateDeletedAt(Workspace workspace) {
        workspace.setDeletedAt(LocalDateTime.now());

        workspaceRepository.save(workspace);
    }
}
