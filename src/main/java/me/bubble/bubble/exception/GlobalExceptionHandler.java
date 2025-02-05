package me.bubble.bubble.exception;

import me.bubble.bubble.dto.response.ApiResponse;
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

    @ExceptionHandler(PathTooLongException.class)
    public ResponseEntity<ApiResponse<Void>> handlePathTooLongException (PathTooLongException pathTooLongException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("PATH_TOO_LONG")
                .message("Path는 255자 이상이어서는 안됩니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(FileDownloadFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileDownloadFailedException(FileDownloadFailedException fileDownloadFailedException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("FILE_DOWNLOAD_FAILED")
                .message("파일 다운로드 중 에러가 발생했습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @ExceptionHandler(FileMoveException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileMoveException(FileMoveException fileMoveException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("FILE_MOVE_FAILED")
                .message("파일을 이동 중 에러가 발생했습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileNotFoundException(FileNotFoundException fileNotFoundException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("FILE_NOT_FOUND")
                .message("파일을 찾지 못하였습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(FileNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileNotSupportedException(FileNotSupportedException fileNotSupportedException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("FILE_NOT_SUPPORTED")
                .message("지원하지 않는 파일 형식입니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(FileUploadFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileUploadFailedException(FileUploadFailedException fileUploadFailedException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("FILE_UPLOAD_FAILED")
                .message("파일 업로드 중 에러가 발생하였습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(TempFileAccessedException.class)
    public ResponseEntity<ApiResponse<Void>> handleTempFileAccessedException(TempFileAccessedException tempFileAccessedException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("TEMP_FILE_ACCESSED")
                .message("임시 파일은 조회할 수 없습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(PictureNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePictureNotFoundException(PictureNotFoundException pictureNotFoundException) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("PICTURE_NOT_FOUND")
                .message("사진을 찾지 못하였습니다.")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
