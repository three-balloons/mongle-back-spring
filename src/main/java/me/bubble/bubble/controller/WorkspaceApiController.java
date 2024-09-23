package me.bubble.bubble.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.User;
import me.bubble.bubble.domain.Workspace;
import me.bubble.bubble.dto.response.ApiResponse;
import me.bubble.bubble.dto.request.PutWorkspaceRequest;
import me.bubble.bubble.dto.response.WorkspaceResponse;
import me.bubble.bubble.exception.InappropriateUserException;
import me.bubble.bubble.service.UserService;
import me.bubble.bubble.service.WorkspaceService;
import me.bubble.bubble.util.SecurityUtil;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
        WorkspaceResponse workspaceResponse = workspaceService.findWorkspaceById(workspaceId);
        return ApiResponse.<WorkspaceResponse>builder()
                .code("OK")
                .message("")
                .data(workspaceResponse)
                .build();
    }

    @GetMapping()
    @Operation(summary = "워크스페이스에 대한 정보 모두 가져오기", description = "해당 유저에 대한 워크스페이스에 대한 정보 모두 가져오기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\" ", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<List<WorkspaceResponse>> getWorkspaces() {
        List<WorkspaceResponse> workspaceList = workspaceService.getAllWorkspacesByUser();
        return ApiResponse.<List<WorkspaceResponse>>builder()
                .code("OK")
                .message("")
                .data(workspaceList)
                .build();
    }

    @GetMapping("/deleted")
    @Operation(summary = "삭제된 워크스페이스 모두 가져오기", description = "해당 유저의 삭제된 워크스페이스 모두 가져오기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\" ", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<List<WorkspaceResponse>> getDeletedWorkspaces() {
        List<WorkspaceResponse> workspaceList = workspaceService.getAllDeletedWorkspacesByUser();
        return ApiResponse.<List<WorkspaceResponse>>builder()
                .code("OK")
                .message("")
                .data(workspaceList)
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
        WorkspaceResponse workspaceResponse = workspaceService.updateNameAndTheme(workspaceId, request.getName(), request.getTheme());
        return ApiResponse.<WorkspaceResponse>builder()
                .code("OK")
                .message("")
                .data(workspaceResponse)
                .build();

    }

    @DeleteMapping("/{workspaceId}")
    @Operation(summary = "워크스페이스 삭제하기", description = "워크스페이스 삭제하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\" ", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(NOT_EXIST)", description = "code: \"NOT_EXIST\", message: \"해당 워크스페이스가 존재하지 않습니다.\" ", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<Void> deleteWorkspace (@PathVariable UUID workspaceId) {
            workspaceService.updateDeletedAt(workspaceId);
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
    public ApiResponse<WorkspaceResponse> createWorkspace (@RequestBody PutWorkspaceRequest request) {
        WorkspaceResponse workspaceResponse = workspaceService.createWorkspace(request);
        return ApiResponse.<WorkspaceResponse>builder()
                .code("OK")
                .message("")
                .data(workspaceResponse)
                .build();
    }
}
