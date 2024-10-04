package me.bubble.bubble.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.User;
import me.bubble.bubble.domain.Workspace;
import me.bubble.bubble.dto.request.PutWorkspaceRequest;
import me.bubble.bubble.dto.response.WorkspaceResponse;
import me.bubble.bubble.exception.InappropriateUserException;
import me.bubble.bubble.exception.WorkspaceNotFoundException;
import me.bubble.bubble.repository.WorkspaceRepository;
import me.bubble.bubble.util.SecurityUtil;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final UserService userService;

    public WorkspaceResponse findWorkspaceById(UUID id) {
        Workspace workspace =  workspaceRepository.findById(id)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace Not Found " + id));

        if (!SecurityUtil.getCurrentUserOAuthId().equals(workspace.getUser().getOauthId())) {
            throw new InappropriateUserException("Inappropriate user");
        }

        return new WorkspaceResponse(workspace);
    }

    public Workspace findWorkspaceEntityById(UUID id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace Not Found " + id));
    }

    public List<WorkspaceResponse> getAllWorkspacesByUser() {
        List<WorkspaceResponse> responseList = new ArrayList<>();
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        User user = userService.findUserEntityByOAuthId(oAuthId);
        List<Workspace> workspaceList = workspaceRepository.findAllByUserOrderByUpdatedAtDesc(user);
        for (Workspace workspace: workspaceList) {
            if (workspace.getDeletedAt() == null)
                responseList.add(new WorkspaceResponse(workspace));
        }
        return responseList;
    }

    public List<WorkspaceResponse> getAllDeletedWorkspacesByUser() {
        List<WorkspaceResponse> responseList = new ArrayList<>();
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        User user = userService.findUserEntityByOAuthId(oAuthId);
        List<Workspace> workspaceList = workspaceRepository.findAllByUserOrderByUpdatedAtDesc(user);
        for (Workspace workspace: workspaceList) {
            if (workspace.getDeletedAt() != null)
                responseList.add(new WorkspaceResponse(workspace));
        }
        return responseList;
    }

    @Transactional
    public WorkspaceResponse updateNameAndTheme(UUID workspaceId, String name, String theme) {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace Not Found " + workspaceId));
        if (!oAuthId.equals(workspace.getUser().getOauthId())) {
            throw new InappropriateUserException("Inappropriate User");
        }
        workspace.setName(name);
        workspace.setTheme(theme);

        // 변경 사항 저장
        return new WorkspaceResponse(workspaceRepository.save(workspace));
    }

    @Transactional
    public WorkspaceResponse createWorkspace(PutWorkspaceRequest request) {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        User user = userService.findUserEntityByOAuthId(oAuthId);
        // Workspace 생성
        Workspace workspace = Workspace.builder()
                .name(request.getName())
                .theme(request.getTheme())
                .deletedAt(null)
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();

        // 저장
        return new WorkspaceResponse(workspaceRepository.save(workspace));
    }

    public void updateDeletedAt(UUID workspaceId) {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace Not Found " + workspaceId));
        if (!oAuthId.equals(workspace.getUser().getOauthId())) {
            throw new InappropriateUserException("Inappropriate user");
        }
        workspace.setDeletedAt(LocalDateTime.now());

        workspaceRepository.save(workspace);
    }

    public void restoreDeletedAt(UUID workspaceId) {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace Not Found " + workspaceId));
        if (!oAuthId.equals(workspace.getUser().getOauthId())) {
            throw new InappropriateUserException("Inappropriate user");
        }

        workspace.setDeletedAt(null);

        workspaceRepository.save(workspace);
    }
    public String getOAuthIdByWorkspaceId(UUID workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace Not Found " + workspaceId));

        return workspace.getUser().getOauthId();
    }
}
