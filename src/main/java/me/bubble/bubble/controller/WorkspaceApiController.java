package me.bubble.bubble.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.User;
import me.bubble.bubble.domain.Workspace;
import me.bubble.bubble.dto.ApiResponse;
import me.bubble.bubble.dto.PutWorkspaceRequest;
import me.bubble.bubble.dto.WorkspaceResponse;
import me.bubble.bubble.exception.InappropriateUserException;
import me.bubble.bubble.service.UserService;
import me.bubble.bubble.service.WorkspaceService;
import me.bubble.bubble.util.SecurityUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workspace")
public class WorkspaceApiController {
    private final WorkspaceService workspaceService;
    private final UserService userService;

    @GetMapping("/{workspaceId}")
    @Operation(summary = "워크스페이스에 대한 정보 가져오기", description = "워크스페이스에 대한 정보 가져오기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(NOT_EXIST)", description = "code: \"NOT_EXIST\", message: \"해당 워크스페이스가 존재하지 않습니다.\"", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\" ", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<WorkspaceResponse> getWorkspace(@PathVariable UUID workspaceId) {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        Workspace workspace = workspaceService.findWorkspaceById(workspaceId);

        if (!oAuthId.equals(workspace.getUser().getOauthId())) {
            throw new InappropriateUserException("Inappropriate user");
        }

        WorkspaceResponse response = new WorkspaceResponse(workspace);

        return ApiResponse.<WorkspaceResponse>builder()
                .code("OK")
                .message("")
                .data(response)
                .build();
    }

    @GetMapping()
    @Operation(summary = "워크스페이스에 대한 정보 모두 가져오기", description = "해당 유저에 대한 워크스페이스에 대한 정보 모두 가져오기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\" ", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<List<WorkspaceResponse>> getWorkspaces() {
        List<WorkspaceResponse> responseList = new ArrayList<>();
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        User user = userService.findUserByOauthId(oAuthId);

        List<Workspace> workspaceList = workspaceService.getAllWorkspacesByUser(user);

        for (Workspace workspace: workspaceList) {
            responseList.add(new WorkspaceResponse(workspace));
        }

        return ApiResponse.<List<WorkspaceResponse>>builder()
                .code("OK")
                .message("")
                .data(responseList)
                .build();
    }

    @PutMapping("/{workspaceId}")
    @Operation(summary = "워크스페이스에 대한 정보 수정하기", description = "워크스페이스 정보 수정하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\" ", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(ALREADY_EXIST)", description = "code: \"ALREADY_EXIST\", message: \"해당 이름의 워크스페이스가 이미 존재합니다.\" ", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<WorkspaceResponse> putWorkspace (@PathVariable UUID workspaceId,
                                                        @RequestBody PutWorkspaceRequest request) {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        Workspace workspace = workspaceService.findWorkspaceById(workspaceId);
        if (!oAuthId.equals(workspace.getUser().getOauthId())) {
            throw new InappropriateUserException("Inappropriate User");
        }

        workspace = workspaceService.updateNameAndTheme(workspace, request.getName(), request.getTheme());
        WorkspaceResponse response = new WorkspaceResponse(workspace);
        return ApiResponse.<WorkspaceResponse>builder()
                .code("OK")
                .message("")
                .data(response)
                .build();

    }

    @DeleteMapping("/{workspaceId}")
    @Operation(summary = "워크스페이스 삭제하기", description = "워크스페이스 삭제하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\" ", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(NOT_EXIST)", description = "code: \"NOT_EXIST\", message: \"해당 워크스페이스가 존재하지 않습니다.\" ", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<Void> deleteWorkspace (@PathVariable UUID workspaceId) {
            String oAuthId = SecurityUtil.getCurrentUserOAuthId();
            Workspace workspace = workspaceService.findWorkspaceById(workspaceId);

            if (!oAuthId.equals(workspace.getUser().getOauthId())) {
                throw new InappropriateUserException("Inappropriate user");
            }
            workspaceService.updateDeletedAt(workspace);
            return ApiResponse.<Void>builder()
                    .code("OK")
                    .message("")
                    .data(null)
                    .build();
    }

    @PostMapping()
    @Operation(summary = "워크스페이스 생성하기", description = "워크스페이스 생성하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\" ", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(ALREADY_EXIST)", description = "code: \"ALREADY_EXIST\", message: \"해당 이름의 워크스페이스가 이미 존재합니다.\" ", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<WorkspaceResponse> postWorkspace (@RequestBody PutWorkspaceRequest request) {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        User user = userService.findUserByOauthId(oAuthId);

        Workspace workspace = workspaceService.createWorkspace(request.getName(), request.getTheme(), user);
        WorkspaceResponse response = new WorkspaceResponse(workspace);

        return ApiResponse.<WorkspaceResponse>builder()
                .code("OK")
                .message("")
                .data(response)
                .build();
    }
}
