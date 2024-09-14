package me.bubble.bubble.exception;

import me.bubble.bubble.domain.Bubble;
import me.bubble.bubble.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(WorkspaceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleWorkspaceNotFoundException(WorkspaceNotFoundException workspaceNotFoundException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("WORKSPACE_NOT_FOUND")
                .message("해당 워크스페이스가 존재하지 않습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotAuthenticatedException(UserNotAuthenticatedException userNotAuthenticatedException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("AUTHENTICATION_FAILED")
                .message("유저 정보를 가져오는 데 실패하였습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(InappropriateUserException.class)
    public ResponseEntity<ApiResponse<Void>> handleInappropriateUserException(InappropriateUserException inappropriateUserException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("INAPPROPRIATE_USER")
                .message("부적절한 유저입니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(InappropriateDepthException.class)
    public ResponseEntity<ApiResponse<Void>> handleInappropriateDepthException(InappropriateDepthException inappropriateDepthException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("INAPPROPRIATE_DEPTH")
                .message("부적절한 깊이입니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(InappropriatePathException.class)
    public ResponseEntity<ApiResponse<Void>> handleInappropriatePathException(InappropriatePathException inappropriatePathException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("INAPPROPRIATE_PATH")
                .message("부적절한 경로입니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(BubbleNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleBubbleNotFoundException(BubbleNotFoundException bubbleNotFoundException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("BUBBLE_NOT_FOUND")
                .message("버블이 존재하지 않습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(NoBubbleInWorkspaceException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoBubbleInWorkspaceException(NoBubbleInWorkspaceException noBubbleInWorkspaceException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("NO_BUBBLE_IN_WORKSPACE")
                .message("해당 워크스페이스에 버블이 존재하지 않습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(BubbleAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Void>> handleBubbleAlreadyExistException(BubbleAlreadyExistException bubbleAlreadyExistException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("BUBBLE_ALREADY_EXIST")
                .message("해당 버블이 이미 존재합니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(BubbleNoParentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBubbleNoParentException (BubbleNoParentException bubbleNoParentException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("BUBBLE_NO_PARENT")
                .message("해당 버블의 부모 버블이 존재하지 않습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(CurveNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCurveNotFoundException (CurveNotFoundException curveNotFoundException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("CURVE_NOT_FOUND")
                .message("해당 커브가 존재하지 않습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException (UserNotFoundException userNotFoundException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("USER_NOT_FOUND")
                .message("해당 유저가 존재하지 않습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(NoSubException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoSubException (NoSubException noSubException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("NO_SUB")
                .message("유저 식별자가 전달되지 않았습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @ExceptionHandler(InappropriatePayloadException.class)
    public ResponseEntity<ApiResponse<Void>> handleInappropriatePayloadException (InappropriatePayloadException inappropriatePayloadException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("INAPPROPRIATE_PAYLOAD")
                .message("부적절한 요청입니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(InappropriateProviderException.class)
    public ResponseEntity<ApiResponse<Void>> handleInappropriateProviderException (InappropriateProviderException inappropriateProviderException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("INAPPROPRIATE_PROVIDER")
                .message("부적절한 제공자입니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
