package me.bubble.bubble.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.dto.response.*;
import me.bubble.bubble.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileApiController {
    private final FileService fileService;
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
}
