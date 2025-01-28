package me.bubble.bubble.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.dto.request.PostFileRequest;
import me.bubble.bubble.dto.response.*;
import me.bubble.bubble.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileApiController {
    private final FileService fileService;

//    @PostMapping
//    public ResponseEntity<String> uploadFile(MultipartFile multipartFile){
//        return ResponseEntity.ok((fileService.uploadFile(multipartFile)));
//    }
//    @DeleteMapping
//    public ResponseEntity<String> deleteFile(@RequestParam String fileName){
//        fileService.deleteFile(fileName);
//        return ResponseEntity.ok(fileName);
//    }
//
    @PostMapping("/temporary")
    @Operation(summary = "임시 파일 추가하기", description = "임시 파일 s3와 DB에 추가하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\"", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<TempFileResponse> uploadTempFile(MultipartFile multipartFile) {
        TempFileResponse tempFileResponse = fileService.uploadTempFile(multipartFile);
        return ApiResponse.<TempFileResponse>builder()
                .code("OK")
                .message("")
                .data(tempFileResponse)
                .build();
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "파일 조회하기", description = "fileId로 파일 조회하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\"", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<FileDataResponse> getFile(@PathVariable(required = true) Long fileId) {
        FileDataResponse fileDataResponse = fileService.getFileById(fileId);

        return ApiResponse.<FileDataResponse>builder()
                .code("OK")
                .message("")
                .data(fileDataResponse)
                .build();
    }

    @PostMapping("/{workspaceId}")
    @Operation(summary = "파일 생성하기", description = "파일 생성하기, S3에 파일 위치 확정시키기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\"", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<PostFileResponse> postFile(@PathVariable(required = true) UUID workspaceId,
                                                  @RequestBody PostFileRequest request) {
        PostFileResponse postFileResponse = fileService.postFile(workspaceId, request);

        return ApiResponse.<PostFileResponse>builder()
                .code("OK")
                .message("")
                .data(postFileResponse)
                .build();
    }

    @GetMapping("/info/{workspaceId}/{fileId}")
    @Operation(summary = "파일 정보 가져오기", description = "파일 정보 가져오기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\"", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<PostFileResponse> getFileInfo(@PathVariable(required = true) UUID workspaceId,
                                                     @PathVariable(required = true) Long fileId) {
        PostFileResponse fileResponse = fileService.getFileInfoById(workspaceId, fileId);
        return ApiResponse.<PostFileResponse>builder()
                .code("OK")
                .message("")
                .data(fileResponse)
                .build();
    }
    @DeleteMapping("/{workspaceId}/{fileId}")
    @Operation(summary = "파일 삭제하기", description = "파일 삭제하기(DB + S3)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\"", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<Void> deleteFile(@PathVariable(required = true) UUID workspaceId,
                                        @PathVariable(required = true) Long fileId) {
        fileService.deleteFileById(workspaceId, fileId);
        return ApiResponse.<Void>builder()
                .code("OK")
                .message("")
                .data(null)
                .build();
    }

}
