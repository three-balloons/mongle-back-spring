package me.bubble.bubble.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.Bubble;
import me.bubble.bubble.domain.Curve;
import me.bubble.bubble.domain.Workspace;
import me.bubble.bubble.dto.*;
import me.bubble.bubble.exception.*;
import me.bubble.bubble.service.BubbleService;
import me.bubble.bubble.service.CurveService;
import me.bubble.bubble.service.WorkspaceService;
import me.bubble.bubble.util.SecurityUtil;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor // 빈 자동 주입 (final이 붙거나 @NotNull이 붙은 필드 대상) (생성자 주입)
@RestController //HTTP Response Body의 객체 데이터를 JSON 형식으로 반환
@RequestMapping("/api/bubble")
public class BubbleApiController {

    private final BubbleService bubbleService;
    private final CurveService curveService;
    private final WorkspaceService workspaceService;

    @GetMapping("/{workspaceId}")
    @Operation(summary = "버블 정보 가져오기", description = "버블과 그 버블에 포함된 버블, 커브 정보 가져오기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\"", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "path", description = "버블의 path", example = "/ws1/A", required = true),
            @Parameter(name = "depth", description = "버블의 depth (1~5 사이, 기본값: 1)", example = "3", required = false)
    })
    public ApiResponse<List<BubbleInfoResponse>> GetBubble(@PathVariable UUID workspaceId,
                                                           @RequestParam(required = true) String path,
                                                           @RequestParam(required = false, defaultValue = "1") int depth)
    {
        Workspace workspace = workspaceService.findWorkspaceById(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspace.getUser().getOauthId())) {
            throw new InappropriateUserException("Inappropriate User");
        }

        if (depth < 1 || depth > 5) {
            throw new InappropriateDepthException("Inappropriate Depth");
        }

        List<Bubble> bubbles = bubbleService.getBubblesByWorkspaceAndPathAndPathDepth(workspaceId, path, depth);
        if (bubbles.isEmpty() && !path.equals("/")) {
            throw new InappropriatePathException("Inappropriate Path");
        }
        List<BubbleInfoResponse> bubbleResponse = new ArrayList<>();
        for (Bubble bubble: bubbles) {
            bubbleResponse.add(buildBubbleResponse(bubble));
        }

        return ApiResponse.<List<BubbleInfoResponse>>builder()
                .code("OK")
                .message("")
                .data(bubbleResponse)
                .build();
    }

    private BubbleInfoResponse buildBubbleResponse(Bubble bubble) {
        List<CurveInfoResponse> curveResponses = new ArrayList<>();

        for (Curve curve : curveService.findCurvesByBubble(bubble)) {
            curveResponses.add(new CurveInfoResponse(curve));
        }
        return new BubbleInfoResponse(bubble, curveResponses);
    }

    @DeleteMapping("/{workspaceId}")
    @Operation(summary = "버블 삭제하기", description = "버블과 그 버블에 포함된 버블, 커브 삭제하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\"", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "path", description = "버블의 path (필수)", example = "/ws1/A", required = true)
    })
    public ApiResponse<Void> DeleteBubble(@PathVariable UUID workspaceId,
                                                         @RequestParam(required = true) String path){

        Workspace workspace = workspaceService.findWorkspaceById(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspace.getUser().getOauthId())) {
            throw new InappropriateUserException("Inappropriate User");
        }

        if (path.endsWith("/")) {
            throw new InappropriatePathException("Inappropriate path");
        }
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
    public ApiResponse<?> GetBubbleTree(@PathVariable UUID workspaceId,
                                        @RequestParam(required = false, defaultValue = "/") String path,
                                        @RequestParam(required = false, defaultValue = "-1") int depth)
    // RequestedParam 내부에는 정적이 값이 들어가야해서 음수로 설정 후 밑에서 음수일 경우 기본값을 바꿔주는 형식으로 구현
    {
        Workspace workspace = workspaceService.findWorkspaceById(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspace.getUser().getOauthId())) {
            throw new InappropriateUserException("Inappropriate user");
        }

        if (depth < -1 || depth == 0) {
            throw new InappropriateDepthException("Inappropriate depth");
        }
        int max_depth = bubbleService.getMaxPathDepth(workspaceId);

        if (depth == -1) { // depth 기본값을 pathDepth의 최대값으로
            depth = max_depth;
        }

        // 응답 객체 만드는 과정
        if (!path.equals("/")) { //특정 path로 요청했을 경우
            Bubble bubble = bubbleService.findByPathAndWorkspaceId(path, workspaceId); //해당 workspace의 특정 path에 해당하는 bubble 찾는다.

            //그 bubble에 대한 응답 객체 생성
            List<BubbleTreeResponse> bubbleTreeResponses = buildBubbleTreeResponseList(bubble, depth, workspaceId);

            return ApiResponse.<List<BubbleTreeResponse>>builder()
                    .code("OK")
                    .message("특정 path로의 요청")
                    .data(bubbleTreeResponses)
                    .build();

        } else{ // 기본 path로의 요청
            //pathDepth가 1인 버블 객체를 가져온 후
            List<Bubble> bubbles = bubbleService.findBubblesByPathDepthAndWorkspaceId(1, workspaceId);
            List<List<BubbleTreeResponse>> bubbleTreeResponses = new ArrayList<>();

            for (Bubble bubble: bubbles) {
                //그 버블들에 대한 응답 객체를 만든다.
                bubbleTreeResponses.add(buildBubbleTreeResponseList(bubble, depth, workspaceId));
            }
            return ApiResponse.<List<List<BubbleTreeResponse>>>builder()
                    .code("OK")
                    .message("기본 path('/')로의 요청")
                    .data(bubbleTreeResponses)
                    .build();
        }
    }

    private List<BubbleTreeResponse> buildBubbleTreeResponseList(Bubble bubble, int depth, UUID workspaceId) {
        if (depth == 0 || bubble == null) {
            return Collections.emptyList();
        }

        List<BubbleTreeResponse> bubbleTreeResponses = new ArrayList<>();
        bubbleTreeResponses.add(buildBubbleTreeResponse(bubble, depth, workspaceId));

        return bubbleTreeResponses;
    }

    private BubbleTreeResponse buildBubbleTreeResponse(Bubble bubble, int depth, UUID workspaceId) {
        if (depth == 0 || bubble == null) {
            return new BubbleTreeResponse(bubble.getName(), null);
        }

        List<BubbleTreeResponse> childrenResponses = new ArrayList<>();
        for (Bubble child : bubbleService.findChildrenByBubbleAndWorkspaceId(bubble, workspaceId)) {
            childrenResponses.add(buildBubbleTreeResponse(child, depth-1, workspaceId));
        }
        return new BubbleTreeResponse(bubble.getName(), childrenResponses);
    }

    @PostMapping("/{workspaceId}")
    @Operation(summary = "버블 생성하기", description = "버블 생성하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\"", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "path", description = "버블의 path (필수)", example = "/ws1/A", required = true)
    })
    public ApiResponse<BubbleInfoResponse> AddBubble (@PathVariable UUID workspaceId,
                                                      @RequestParam(required = true) String path,
                                                      @RequestBody BubbleAddRequest request) {
        // 해당 workspace 가져온다.
        Workspace workspace = workspaceService.findWorkspaceById(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspace.getUser().getOauthId())) {
            throw new InappropriateUserException("Inappropriate user");
        }

        if (path.lastIndexOf('/') == -1 || path.endsWith("/")) { // request의 path가 hhh같은 경우와 /로 끝나는 경우
            throw new InappropriatePathException("Inappropriate path");
        }

        String tempString = path.substring(0, path.lastIndexOf('/')); //path 파싱해서 마지막 / 전까지 문자열 가져온다.

        if (tempString.isEmpty()) { // /ws1 이런 식으로 위의 부모가 없는 버블일 경우 (1단계 버블일 경우).
            try { //예외처리 (이미 존재하는 버블일 경우), 그렇지 않으면 새로운 버블 생성
                Bubble existingBubble = bubbleService.findByPathAndWorkspaceId(path, workspaceId);
                throw new BubbleAlreadyExistException("Bubble already exists");

            } catch (BubbleNotFoundException ex) {
                // 버블이 존재하지 않는 경우 새로운 버블을 생성
                Bubble bubble = Bubble.builder()
                        .name(request.getName())
                        .top(request.getTop())
                        .leftmost(request.getLeft())
                        .width(request.getWidth())
                        .height(request.getHeight())
                        .path(path)
                        .pathDepth(1)
                        .workspace(workspace)
                        .bubblized(request.isBubblized())
                        .visible(request.isVisible())
                        .build();

                Bubble savedBubble = bubbleService.saveBubble(bubble);

                BubbleInfoResponse bubbleResponse = new BubbleInfoResponse(savedBubble,null);
                return ApiResponse.<BubbleInfoResponse>builder()
                        .code("OK")
                        .message("")
                        .data(bubbleResponse)
                        .build();
            }
        }
        else{
            try { //예외처리 (부모가 없는 경우)
                Bubble parentBubble = bubbleService.findByPathAndWorkspaceId(tempString, workspaceId);

                try { //예외처리 (이미 존재하는 버블일 경우), 그렇지 않으면 새로운 버블 생성
                    Bubble existingBubble = bubbleService.findByPathAndWorkspaceId(path, workspaceId);
                    throw new BubbleAlreadyExistException("Bubble already exists");

                } catch (BubbleNotFoundException ex) {
                    // 버블이 존재하지 않는 경우 새로운 버블을 생성
                    Bubble bubble = Bubble.builder()
                            .name(request.getName())
                            .top(request.getTop())
                            .leftmost(request.getLeft())
                            .width(request.getWidth())
                            .height(request.getHeight())
                            .path(path)
                            .pathDepth(parentBubble.getPathDepth()+ 1)
                            .workspace(workspace)
                            .bubblized(request.isBubblized())
                            .visible(request.isVisible())
                            .build();

                    Bubble savedBubble = bubbleService.saveBubble(bubble);

                    BubbleInfoResponse bubbleResponse = new BubbleInfoResponse(savedBubble,null);
                    return ApiResponse.<BubbleInfoResponse>builder()
                            .code("OK")
                            .message("")
                            .data(bubbleResponse)
                            .build();
                }
            } catch (BubbleNotFoundException ex) {
                throw new BubbleNoParentException("Bubble no parent");
            }
        }
    }

    @PutMapping("/{workspaceId}/curve")
    @Operation(summary = "버블 속 커브 업데이트", description = "버블에 포함된 커브 수정하기")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200(OK)", description = "code: \"OK\", message: \"\"", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "path", description = "버블의 path", example = "/ws1/A", required = true)
    })
    public ApiResponse<PutResponse> PutBubble (@PathVariable UUID workspaceId,
                                               @RequestParam(required = true) String path,
                                               @RequestBody PutRequest request) {
        Workspace workspace = workspaceService.findWorkspaceById(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspace.getUser().getOauthId())) {
            throw new InappropriateUserException("Inappropriate user");
        }

        Bubble bubble = bubbleService.findByPathAndWorkspaceId(path, workspaceId);

        List<PutResponseObject> deleteList = new ArrayList<>();
        List<PutResponseObject> updateList = new ArrayList<>();
        List<PutResponseObject> createList = new ArrayList<>();
        String responseMessage = "";

        for (PutDeleteRequest delete: request.getDelete()) { // curveId로 찾아서 삭제, 없으면 예외처리를 통해서 successYn을 false로
            try {
                curveService.deleteCurveById(delete.getId());
                PutResponseObject putResponseObject = new PutResponseObject(delete.getId(), true);
                deleteList.add(putResponseObject);
            }
            catch (CurveNotFoundException ex) {
                responseMessage = "저장에 실패한 코드가 존재합니다.";
                PutResponseObject putResponseObject = new PutResponseObject(delete.getId(), false);
                deleteList.add(putResponseObject);
            }
        }

        for (PutUpdateRequest update: request.getUpdate()) {
            Long modifiedCurveId = update.getId();//request로부터 커브 정보 가져오기
            CurveInfoResponse modifiedCurve = update.getCurve();
            try {
                Curve curve = curveService.findCurveById(modifiedCurveId); // 실제 커브 객체 가져오기
                String controlPoint = "";
                for (ControlPoint control: modifiedCurve.getPosition()) {
                    controlPoint = controlPoint + control.toBinaryString();
                }
                curve = curveService.updateCurve(modifiedCurveId, modifiedCurve.getConfig().getColor(),
                        modifiedCurve.getConfig().getThickness(), bubble, controlPoint);
                PutResponseObject putResponseObject = new PutResponseObject(update.getId(), true);
                updateList.add(putResponseObject);

            } catch (CurveNotFoundException ex) {
                responseMessage = "저장에 실패한 코드가 존재합니다.";
                PutResponseObject putResponseObject = new PutResponseObject(update.getId(), false);
                updateList.add(putResponseObject);
            }
        }

        for (PutCreateRequest create: request.getCreate()) {

            CurveInfoResponse modifiedCurve = create.getCurve();

            String controlPoint = "";
            for (ControlPoint control: modifiedCurve.getPosition()) {
                controlPoint = controlPoint + control.toBinaryString();
            }
            Curve curve = Curve.builder()
                    .color(modifiedCurve.getConfig().getColor())
                    .thickness(modifiedCurve.getConfig().getThickness())
                    .bubble(bubble)
                    .controlPoint(controlPoint)
                    .build();

            Curve savedCurve = curveService.saveCurve(curve);

            PutResponseObject putResponseObject = new PutResponseObject(savedCurve.getId(), true);
            createList.add(putResponseObject);
        }

        PutResponse putResponse = new PutResponse(deleteList, updateList, createList);
        return ApiResponse.<PutResponse>builder()
                .code("OK")
                .message(responseMessage)
                .data(putResponse)
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
    public ApiResponse<Object> MoveBubble (@PathVariable UUID workspaceId,
                                               @RequestParam(required = true) String oldPath,
                                               @RequestBody PutMoveRequest request) {

        Workspace workspace = workspaceService.findWorkspaceById(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspace.getUser().getOauthId())) {
            throw new InappropriateUserException("Inappropriate user");
        }

        Bubble bubble = bubbleService.findByPathAndWorkspaceId(oldPath, workspaceId);

        if (request.getNewPath().isEmpty()) {
            bubbleService.updateBubble(bubble.getId(), request.getName(), request.getTop(), request.getLeft(),
                    request.getWidth(), request.getHeight(), oldPath, bubble.getPathDepth(), request.isBubblized(),
                    request.isVisible(), workspace);
        } else {
            bubbleService.updateBubblePaths(workspace, oldPath, request.getNewPath());
            bubbleService.updateBubble(bubble.getId(), request.getName(), request.getTop(), request.getLeft(),
                    request.getWidth(), request.getHeight(), request.getNewPath(), countOccurrences(request.getNewPath(), '/'),
                    request.isBubblized(), request.isVisible(), workspace);
        }


        return ApiResponse.<Object>builder() // Workspace는 적절히 주어진다고 가정.
                .code("OK")
                .message("")
                .data(null)
                .build();

    }
    private int countOccurrences (String str,char character){
        return (int) str.chars().filter(ch -> ch == character).count();
    }
}
