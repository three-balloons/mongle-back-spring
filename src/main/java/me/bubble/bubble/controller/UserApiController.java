package me.bubble.bubble.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.dto.response.ApiResponse;
import me.bubble.bubble.dto.response.UserResponse;
import me.bubble.bubble.dto.request.PutUserRequest;
import me.bubble.bubble.service.UserService;
import me.bubble.bubble.util.SecurityUtil;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<UserResponse> getUser() {
        String oAuthId = SecurityUtil.getCurrentUserOAuthId();
        UserResponse user = userService.findUserByOauthId(oAuthId);
        return ApiResponse.<UserResponse>builder()
                .code("OK")
                .message("")
                .data(user)
                .build();
    }

    @PutMapping()
    @Operation(summary = "유저 정보 수정하기", description = "유저 정보 수정하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(USER_NOT_FOUND)", description = "code: \"USER_NOT_FOUND\", message: \"유저가 존재하지 않습니다.\"", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\" ", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<UserResponse> putUser(@RequestBody PutUserRequest request) {

        UserResponse user = userService.updateUserNameAndEmail(request);
        return ApiResponse.<UserResponse>builder()
                .code("OK")
                .message("")
                .data(user)
                .build();

    }

    @DeleteMapping()
    @Operation(summary = "유저 삭제 업데이트하기", description = "유저 deletedAt 업데이트하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(USER_NOT_FOUND)", description = "code: \"USER_NOT_FOUND\", message: \"유저가 존재하지 않습니다.\"", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\" ", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<Void> deleteUser() {
        userService.updateDeletedAtByOauthId();
        return ApiResponse.<Void>builder()
                .code("OK")
                .message("")
                .data(null)
                .build();
    }
}
