package me.bubble.bubble.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.dto.request.*;
import me.bubble.bubble.dto.response.*;
import me.bubble.bubble.service.BubbleService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor // 빈 자동 주입 (final이 붙거나 @NotNull이 붙은 필드 대상) (생성자 주입)
@RestController //HTTP Response Body의 객체 데이터를 JSON 형식으로 반환
@RequestMapping("/api/bubble")
public class BubbleApiController {

    private final BubbleService bubbleService;

    @GetMapping("/{workspaceId}")
    @Operation(summary = "버블 정보 가져오기", description = "버블과 그 버블에 포함된 버블, 커브 정보 가져오기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\"", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "path", description = "버블의 path", example = "/ws1/A", required = true),
            @Parameter(name = "depth", description = "버블의 depth (1~5 사이, 기본값: 1)", example = "3", required = false)
    })
    public ApiResponse<List<BubbleInfoResponse>> getBubble(@PathVariable("workspaceId") UUID workspaceId,
                                                           @RequestParam(required = true) String path,
                                                           @RequestParam(required = false, defaultValue = "-1") Integer depth) {
        List<BubbleInfoResponse> bubbles = bubbleService.getBubblesByWorkspaceAndPathAndPathDepth(workspaceId, path, depth);
        return ApiResponse.<List<BubbleInfoResponse>>builder()
                .code("OK")
                .message("")
                .data(bubbles)
                .build();
    }

    @DeleteMapping("/{workspaceId}")
    @Operation(summary = "버블 삭제하기", description = "버블과 그 버블에 포함된 버블, 커브 삭제하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\"", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "path", description = "버블의 path (필수)", example = "/ws1/A", required = true)
    })
    public ApiResponse<Void> deleteBubble(@PathVariable UUID workspaceId,
                                          @RequestParam(required = true) String path){
        bubbleService.deleteByPathStartingWithAndWorkspaceId(path, workspaceId);
        return ApiResponse.<Void>builder()
                .code("OK")
                .message("")
                .data(null)
                .build();
    }

    @GetMapping("/tree/{workspaceId}")
    @Operation(summary = "버블 트리 가져오기", description = "Workspace 내의 bubble 트리 구조 가져오기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK_1)", description = "code: \"OK\", message: \"특정 path로의 요청\"", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK_2)", description = "code: \"OK\", message: \"기본 path('/')로의 요청\"", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "path", description = "버블의 path (path가 없을 시 workspace 내 전체 트리 반환)", example = "/ws1/A"),
            @Parameter(name = "depth", description = "탐색을 원하는 깊이 (기본값: 가장 깊은 버블까지)", example = "3"),
    })
    // <?>: 어떤 자료형의 객체도 매개변수로 받겠다는 의미
    public ApiResponse<List<?>> getBubbleTree(@PathVariable UUID workspaceId,
                                        @RequestParam(required = false, defaultValue = "/") String path,
                                        @RequestParam(required = false, defaultValue = "-1") Integer depth)
    // RequestedParam 내부에는 정적이 값이 들어가야해서 음수로 설정 후 밑에서 음수일 경우 기본값을 바꿔주는 형식으로 구현
    {
        BubbleTreeCapsule treeResponse = bubbleService.getBubbleTree(path, workspaceId, depth);
        return ApiResponse.<List<?>>builder()
                .code("OK")
                .message(treeResponse.message())
                .data(treeResponse.bubbleTreeResponse())
                .build();
    }

    @PostMapping("/{workspaceId}")
    @Operation(summary = "버블 생성하기", description = "버블 생성하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\"", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "path", description = "버블의 path (필수)", example = "/ws1/A", required = true)
    })
    public ApiResponse<BubbleInfoResponse> addBubble (@PathVariable UUID workspaceId,
                                                      @RequestParam(required = true) String path,
                                                      @RequestBody BubbleAddRequest request) {
        //addBubble에서 workspace 조회가 필요해서 유저 인증도 addBubble() 내에서 하도록 구현
        BubbleInfoResponse response = bubbleService.addBubble(path, workspaceId, request);
        return ApiResponse.<BubbleInfoResponse>builder()
                .code("OK")
                .message("")
                .data(response)
                .build();
    }

    @PutMapping("/{workspaceId}/curve")
    @Operation(summary = "버블 속 커브 업데이트", description = "버블에 포함된 커브 수정하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\"", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "path", description = "버블의 path", example = "/ws1/A", required = true)
    })
    public ApiResponse<PutResponse> putBubble (@PathVariable UUID workspaceId,
                                               @RequestParam(required = true) String path,
                                               @RequestBody PutRequest request) {
        PutCapsule putCapsule = bubbleService.putBubble(request, path, workspaceId);
        return ApiResponse.<PutResponse>builder()
                .code("OK")
                .message(putCapsule.responseMessage())
                .data(putCapsule.putResponse())
                .build();

    }

    @PutMapping("/{workspaceId}/move")
    @Operation(summary = "버블 위치 옮기기", description = "버블의 위치 옮기기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\"", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "oldPath", description = "버블의 기존 path (필수)", example = "/ws1/A", required = true)
    })
    public ApiResponse<Void> moveBubble (@PathVariable UUID workspaceId,
                                               @RequestParam(required = true) String oldPath,
                                               @RequestBody PutMoveRequest request) {
        bubbleService.moveBubble(request, oldPath, workspaceId);
        return ApiResponse.<Void>builder() // Workspace는 적절히 주어진다고 가정.
                .code("OK")
                .message("")
                .data(null)
                .build();

    }
}
