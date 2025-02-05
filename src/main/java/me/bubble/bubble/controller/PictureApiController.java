package me.bubble.bubble.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.dto.request.PostPictureRequest;
import me.bubble.bubble.dto.response.ApiResponse;
import me.bubble.bubble.dto.response.PictureResponse;
import me.bubble.bubble.service.PictureService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/picture")
public class PictureApiController {
    private final PictureService pictureService;

    @PostMapping("/{workspaceId}")
    @Operation(summary = "이미지 생성하기", description = "이미지 생성하기, S3에 파일 위치 확정시키기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\"", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<PictureResponse> postPicture(@PathVariable(required = true) UUID workspaceId,
                                                    @RequestBody PostPictureRequest request) {
        PictureResponse postPictureResponse = pictureService.postPicture(workspaceId, request);

        return ApiResponse.<PictureResponse>builder()
                .code("OK")
                .message("")
                .data(postPictureResponse)
                .build();
    }

    @GetMapping("/info/{workspaceId}/{pictureId}")
    @Operation(summary = "이미지 정보 가져오기", description = "이미지 정보 가져오기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\"", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<PictureResponse> getPictureInfo(@PathVariable(required = true) UUID workspaceId,
                                                    @PathVariable(required = true) Long pictureId) {
        PictureResponse pictureResponse = pictureService.getPictureInfoById(workspaceId, pictureId);
        return ApiResponse.<PictureResponse>builder()
                .code("OK")
                .message("")
                .data(pictureResponse)
                .build();
    }
    @DeleteMapping("/{workspaceId}/{pictureId}")
    @Operation(summary = "이미지 삭제하기", description = "이미지, 파일 삭제하기(DB + S3)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\"", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<Void> deletePicture(@PathVariable(required = true) UUID workspaceId,
                                        @PathVariable(required = true) Long pictureId) {
        pictureService.deletePictureById(workspaceId, pictureId);
        return ApiResponse.<Void>builder()
                .code("OK")
                .message("")
                .data(null)
                .build();
    }
}
