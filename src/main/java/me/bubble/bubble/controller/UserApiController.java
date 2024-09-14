package me.bubble.bubble.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import me.bubble.bubble.dto.*;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.User;
import me.bubble.bubble.dto.GetUserResponse;
import me.bubble.bubble.service.UserService;
import me.bubble.bubble.util.SecurityUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserApiController {
    private final UserService userService;

    @GetMapping()
    @Operation(summary = "유저 정보 가져오기", description = "유저 정보 가져오기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(USER_NOT_FOUND)", description = "code: \"USER_NOT_FOUND\", message: \"유저가 존재하지 않습니다.\"", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\" ", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<GetUserResponse> getUser() {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        User user = userService.findUserByOauthId(oAuthId);

        GetUserResponse response = new GetUserResponse(user);

        return ApiResponse.<GetUserResponse>builder()
                .code("OK")
                .message("")
                .data(response)
                .build();
    }

    @PutMapping()
    @Operation(summary = "유저 정보 수정하기", description = "유저 정보 수정하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(USER_NOT_FOUND)", description = "code: \"USER_NOT_FOUND\", message: \"유저가 존재하지 않습니다.\"", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\" ", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<GetUserResponse> putUser(@RequestBody PutUserRequest request) {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        User user = userService.updateUserNameAndEmail(oAuthId, request.getName(), request.getEmail());
        GetUserResponse response = new GetUserResponse(user);
        return ApiResponse.<GetUserResponse>builder()
                .code("OK")
                .message("")
                .data(response)
                .build();

    }

    @DeleteMapping()
    @Operation(summary = "유저 삭제 업데이트하기", description = "유저 deletedAt 업데이트하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(USER_NOT_FOUND)", description = "code: \"USER_NOT_FOUND\", message: \"유저가 존재하지 않습니다.\"", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\" ", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<Void> deleteUser() {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        userService.updateDeletedAtByOauthId(oAuthId);

        return ApiResponse.<Void>builder()
                .code("OK")
                .message("")
                .data(null)
                .build();
    }
}
