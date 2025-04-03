package me.bubble.bubble.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.Bubble;
import me.bubble.bubble.domain.File;
import me.bubble.bubble.domain.Picture;
import me.bubble.bubble.dto.request.PostPictureRequest;
import me.bubble.bubble.dto.response.PictureResponse;
import me.bubble.bubble.exception.*;
import me.bubble.bubble.repository.BubbleRepository;
import me.bubble.bubble.repository.FileRepository;
import me.bubble.bubble.repository.PictureRepository;
import me.bubble.bubble.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PictureService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;
    private final PictureRepository pictureRepository;
    private final WorkspaceService workspaceService;
    private final FileRepository fileRepository;
    private final BubbleRepository bubbleRepository;

    @Transactional
    public PictureResponse postPicture (UUID workspaceId, PostPictureRequest request) {
        String workspaceOAuthId = workspaceService.getOAuthIdByWorkspaceId(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspaceOAuthId)) {
            throw new InappropriateUserException("Inappropriate User");
        }

        Long fileId = request.getFid();

        // 파일 ID를 기반으로 DB에서 기존 파일 정보를 조회
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("FILE NOT FOUND"));

        Bubble currentBubble = bubbleRepository.findByPathAndWorkspaceId(request.getPath(), workspaceId)
                .orElseThrow(() -> new BubbleNotFoundException("BUBBLE NOT FOUND"));

        String currentPath = file.getPath();

        if (currentPath.startsWith("/temp")) {
            String targetPath = currentPath.replace("/temp", "/" + userOAuthId + "/" + workspaceId + "/pictures");
            // S3에서 파일 이동
            try {
                moveFileInS3(currentPath, targetPath);
                file.update(targetPath, file.getType(), file.getSize());
                fileRepository.save(file);
            } catch (Exception e) {
                throw new FileMoveException("Failed to move file on S3");
            }
        }
        else {
            throw new FileNotSupportedException("BAD REQUEST");
        }

        Picture picture = new Picture(request.getTop(), request.getLeft(), request.getWidth(), request.getHeight(), request.getIsFlippedX(),
                request.getIsFlippedY(), request.getAngle(), currentBubble, file);

        Picture savedPicture = pictureRepository.save(picture);

        // 응답 반환
        return new PictureResponse(savedPicture);
    }

    public List<Picture> findPicturesByBubble(Bubble bubble) { return pictureRepository.findByBubbleIdOrderByUpdatedAtAsc(bubble.getId());}

    public PictureResponse getPictureInfoById(UUID workspaceId, Long pictureId) {
        String workspaceOAuthId = workspaceService.getOAuthIdByWorkspaceId(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspaceOAuthId)) {
            throw new InappropriateUserException("Inappropriate User");
        }

        Picture picture = pictureRepository.findById(pictureId).orElseThrow(() -> new PictureNotFoundException("PICTURE NOT FOUND"));

        return new PictureResponse(picture);
    }


    private void moveFileInS3(String sourcePath, String targetPath) {
        // Copy the file to the new location
        amazonS3.copyObject(bucket, sourcePath, bucket, targetPath);

        // Delete the original file
        amazonS3.deleteObject(bucket, sourcePath);
    }


    public void deletePictureById(UUID workspaceId, Long pictureId) {
        String workspaceOAuthId = workspaceService.getOAuthIdByWorkspaceId(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspaceOAuthId)) {
            throw new InappropriateUserException("Inappropriate User");
        }

        Picture picture = pictureRepository.findById(pictureId).orElseThrow(() -> new PictureNotFoundException("PICTURE NOT FOUND"));

        String s3_path = picture.getFile().getPath();
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, s3_path));

        pictureRepository.delete(picture);
        fileRepository.delete(picture.getFile());
    }
}
