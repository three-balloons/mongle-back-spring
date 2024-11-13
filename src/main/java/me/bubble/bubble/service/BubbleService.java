package me.bubble.bubble.service;

import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.Bubble;
import me.bubble.bubble.domain.Curve;
import me.bubble.bubble.domain.Workspace;
import me.bubble.bubble.dto.response.ControlPoint;
import me.bubble.bubble.dto.response.PutResponseObject;
import me.bubble.bubble.dto.request.*;
import me.bubble.bubble.dto.response.*;
import me.bubble.bubble.exception.*;
import me.bubble.bubble.repository.BubbleRepository;
import me.bubble.bubble.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BubbleService {
    private final BubbleRepository bubbleRepository;
    private final CurveService curveService;
    private final WorkspaceService workspaceService;

    public List<BubbleInfoResponse> getBubblesByWorkspaceAndPathAndPathDepth(UUID workspaceId, String path, Integer pathDepth) {
        String workspaceOAuthId = workspaceService.getOAuthIdByWorkspaceId(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspaceOAuthId)) {
            throw new InappropriateUserException("Inappropriate User");
        }

        if ((pathDepth != -1) && (pathDepth < 1 || pathDepth > 5)) {
            throw new InappropriateDepthException("Inappropriate Depth");
        }

        List<Bubble> bubbles;
        if (pathDepth == -1) { // -1이면 모든 버블 다 보고 pathDepth 오름차순으로 정리
            bubbles = bubbleRepository.findByWorkspaceIdAndPathStartsWithOrderByPathDepthAsc(workspaceId, path);
        } else { // depth가 주어진 경우, depth보다 작은 버블 보고 pathDepth 오름차순으로 정리
            bubbles =  bubbleRepository.findByWorkspaceIdAndPathStartsWithAndPathDepthLessThanEqualOrderByPathDepthAsc(workspaceId, path, pathDepth);
        }
        if (bubbles.isEmpty() && !path.equals("/")) {
            throw new InappropriatePathException("Inappropriate Path");
        }
        List<BubbleInfoResponse> bubbleResponse = new ArrayList<>();
        for (Bubble bubble: bubbles) {
            bubbleResponse.add(buildBubbleResponse(bubble));
        }
        return bubbleResponse;
    }



    public BubbleInfoResponse addBubble(String path, UUID workspaceId, BubbleAddRequest request) {
        // 해당 workspace 가져온다.
        if (path.length() > 255) {
            throw new PathTooLongException("Path Too Long");
        }

        Workspace workspace = workspaceService.findWorkspaceEntityById(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspace.getUser().getOauthId())) {
            throw new InappropriateUserException("Inappropriate user");
        }
        if (path.lastIndexOf('/') == -1 || path.endsWith("/")) { // request의 path가 hhh같은 경우와 /로 끝나는 경우
            throw new InappropriatePathException("Inappropriate path");
        }

        String tempString = path.substring(0, path.lastIndexOf('/')); //path 파싱해서 마지막 / 전까지 문자열 가져온다.

        if (tempString.isEmpty()) { // /ws1 이런 식으로 위의 부모가 없는 버블일 경우 (1단계 버블일 경우).
            if (bubbleRepository.findByPathAndWorkspaceId(path, workspaceId).isPresent())
                throw new BubbleAlreadyExistException("Bubble Already Exists");
            else {
                // 버블이 존재하지 않는 경우 새로운 버블을 생성
                Bubble newBubble = Bubble.builder()
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
                Bubble savedBubble = bubbleRepository.save(newBubble);
                return new BubbleInfoResponse(savedBubble, null);
            }
        }
        else{
            Bubble parentBubble = bubbleRepository.findByPathAndWorkspaceId(tempString, workspaceId)
                    .orElseThrow(() -> new BubbleNoParentException("Bubble No Parent"));
            if (bubbleRepository.findByPathAndWorkspaceId(path, workspaceId).isPresent())
                throw new BubbleAlreadyExistException("Bubble Already Exists");
            else {
                // 버블이 존재하지 않는 경우 새로운 버블을 생성
                Bubble newBubble = Bubble.builder()
                        .name(request.getName())
                        .top(request.getTop())
                        .leftmost(request.getLeft())
                        .width(request.getWidth())
                        .height(request.getHeight())
                        .path(path)
                        .pathDepth(parentBubble.getPathDepth()+1)
                        .workspace(workspace)
                        .bubblized(request.isBubblized())
                        .visible(request.isVisible())
                        .build();
                Bubble savedBubble = bubbleRepository.save(newBubble);
                return new BubbleInfoResponse(savedBubble, null);
            }
        }
    }

    public PutCapsule putBubble(PutRequest request, String path, UUID workspaceId) {
        String workspaceOAuthId = workspaceService.getOAuthIdByWorkspaceId(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspaceOAuthId)) {
            throw new InappropriateUserException("Inappropriate user");
        }
        Bubble bubble = bubbleRepository.findByPathAndWorkspaceId(path, workspaceId)
                .orElseThrow(() -> new BubbleNotFoundException("Bubble Not Found"));

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
        return new PutCapsule(putResponse, responseMessage);
    }

    @Transactional
    public void deleteByPathStartingWithAndWorkspaceId(String path, UUID workspaceId) {
        String workspaceOAuthId = workspaceService.getOAuthIdByWorkspaceId(workspaceId);
        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspaceOAuthId)) {
            throw new InappropriateUserException("Inappropriate User");
        }
        if (path.endsWith("/")) {
            throw new InappropriatePathException("Inappropriate path");
        }
        bubbleRepository.deleteByPathStartingWithAndWorkspaceId(path, workspaceId);
    }

    public BubbleInfoResponse changeBubbleName(ChangeNameRequest request, String path, UUID workspaceId) {
        String workspaceOAuthId = workspaceService.getOAuthIdByWorkspaceId(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspaceOAuthId)) {
            throw new InappropriateUserException("Inappropriate user");
        }

        Bubble bubble = bubbleRepository.findByPathAndWorkspaceId(path, workspaceId)
                .orElseThrow(() -> new BubbleNotFoundException("Bubble Not Found"));

        bubble.update(request.getName(), bubble.getTop(), bubble.getLeftmost(), bubble.getWidth(), bubble.getHeight(), path, bubble.getPathDepth(),
                     bubble.isBubblized(), bubble.isVisible(), bubble.getWorkspace());
        Bubble savedBubble = bubbleRepository.save(bubble);

        List<CurveInfoResponse> curves = new ArrayList<>();
        for (Curve curve : curveService.findCurvesByBubble(bubble)) {
            curves.add(new CurveInfoResponse(curve));
        }

        return new BubbleInfoResponse(savedBubble, curves);
    }

    public void moveBubble(PutMoveRequest request, String oldPath, UUID workspaceId) {
        String workspaceOAuthId = workspaceService.getOAuthIdByWorkspaceId(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspaceOAuthId)) {
            throw new InappropriateUserException("Inappropriate user");
        }

        Bubble bubble = bubbleRepository.findByPathAndWorkspaceId(oldPath, workspaceId)
                .orElseThrow(() -> new BubbleNotFoundException("Bubble Not Found"));

        int oldPathSlashCount = countOccurrences(oldPath, '/');
        //oldPath로 시작하는 모든 버블들, 즉 해당 버블과 그 자녀들을 가져온다.
        List<Bubble> bubbles = bubbleRepository.findByWorkspaceAndPathStartingWith(bubble.getWorkspace(), oldPath);
        for (Bubble tempBubble : bubbles) {
            int newPathSlashCount = countOccurrences(request.getNewPath(), '/');
            int slashCountDifference = newPathSlashCount - oldPathSlashCount;

            String currentPath = tempBubble.getPath();
            // path 부분을 newPath로 변경
            String updatedPath = currentPath.replaceFirst(oldPath, request.getNewPath());

            if (updatedPath.length() > 255) {
                throw new PathTooLongException("Path Too Long");
            }
            tempBubble.setPath(updatedPath);
            tempBubble.setPathDepth(tempBubble.getPathDepth() + slashCountDifference);
        }
        bubbleRepository.saveAll(bubbles);
        bubble.update(bubble.getName(), request.getTop(), request.getLeft(), request.getWidth(), request.getHeight(), request.getNewPath(), countOccurrences(request.getNewPath(), '/'),
                    request.isBubblized(), request.isVisible(), bubble.getWorkspace());

        bubbleRepository.save(bubble);
    }

    private BubbleInfoResponse buildBubbleResponse(Bubble bubble) {
        List<CurveInfoResponse> curveResponses = new ArrayList<>();

        for (Curve curve : curveService.findCurvesByBubble(bubble)) {
            curveResponses.add(new CurveInfoResponse(curve));
        }
        return new BubbleInfoResponse(bubble, curveResponses);
    }

    private int countOccurrences (String str,char character){
        return (int) str.chars().filter(ch -> ch == character).count();
    }

    public BubbleTreeCapsule getBubbleTree(String path, UUID workspaceId, Integer depth) {
        String workspaceOAuthId = workspaceService.getOAuthIdByWorkspaceId(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspaceOAuthId)) {
            throw new InappropriateUserException("Inappropriate user");
        }
        if (depth < -1 || depth == 0) {
            throw new InappropriateDepthException("Inappropriate depth");
        }

        List<Bubble> bubbles;
        if (depth == -1) {
            bubbles = bubbleRepository.findByWorkspaceIdAndPathStartsWithOrderByPathDepthAsc(workspaceId, path);
        } else {
            bubbles = bubbleRepository.findByWorkspaceIdAndPathStartsWithAndPathDepthLessThanEqualOrderByPathDepthAsc(workspaceId, path, depth);
        }

        if (bubbles.isEmpty()) {
            return new BubbleTreeCapsule(new ArrayList<>(), "버블이 없습니다.");
        }

        // 특정 path 요청인 경우
        if (!path.equals("/")) {
            // 해당 경로의 버블 트리 생성
            Bubble rootBubble = bubbles.stream()
                    .filter(bubble -> bubble.getPath().equals(path))
                    .findFirst()
                    .orElseThrow(() -> new NoBubbleInWorkspaceException("No root bubble found for path " + path));

            BubbleTreeResponse bubbleTree = buildBubbleTree(rootBubble, bubbles);
            return new BubbleTreeCapsule(Collections.singletonList(bubbleTree), "특정 Path로의 요청");

        } else {
            // 기본 path 요청인 경우, pathDepth가 1인 버블들을 각각 트리로 만듬
            List<BubbleTreeResponse> rootBubbles = bubbles.stream()
                    .filter(bubble -> bubble.getPathDepth() == 1)
                    .map(bubble -> buildBubbleTreeForRoot(bubble, bubbles))
                    .collect(Collectors.toList());

            return new BubbleTreeCapsule(rootBubbles, "기본 Path로의 요청");
        }
    }

    private BubbleTreeResponse buildBubbleTreeForRoot(Bubble root, List<Bubble> allBubbles) {
        BubbleTreeResponse rootResponse = new BubbleTreeResponse(root.getName(), root.getPath(),null);

        // 현재 root bubble의 path 다음 깊이의 자식 버블들을 찾아 트리 구조에 추가
        List<BubbleTreeResponse> children = allBubbles.stream()
                .filter(bubble -> bubble.getPath().startsWith(root.getPath() + "/") && bubble.getPathDepth() == root.getPathDepth() + 1)
                .map(bubble -> buildBubbleTreeForRoot(bubble, allBubbles))
                .collect(Collectors.toList());

        rootResponse.setChildren(children);
        return rootResponse;
    }


    private BubbleTreeResponse buildBubbleTree(Bubble rootBubble, List<Bubble> allBubbles) {
        // BubbleTreeResponse는 트리의 노드를 나타냄
        BubbleTreeResponse rootResponse = new BubbleTreeResponse(rootBubble.getName(), rootBubble.getPath(), null);

        // 현재 rootBubble의 path 다음 깊이의 자식 버블들을 찾아 트리 구조에 추가
        List<BubbleTreeResponse> children = allBubbles.stream()
                .filter(bubble -> bubble.getPath().startsWith(rootBubble.getPath() + "/") && bubble.getPathDepth() == rootBubble.getPathDepth() + 1)
                .map(bubble -> buildBubbleTree(bubble, allBubbles))
                .collect(Collectors.toList());

        // 자식 버블들을 rootResponse에 추가
        rootResponse.setChildren(children);

        return rootResponse;
    }


}