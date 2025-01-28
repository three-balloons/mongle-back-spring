package me.bubble.bubble.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.Bubble;
import me.bubble.bubble.domain.Curve;
import me.bubble.bubble.domain.File;
import me.bubble.bubble.dto.request.PostFileRequest;
import me.bubble.bubble.dto.response.FileDataResponse;
import me.bubble.bubble.dto.response.PostFileResponse;
import me.bubble.bubble.dto.response.TempFileResponse;
import me.bubble.bubble.exception.*;
import me.bubble.bubble.repository.BubbleRepository;
import me.bubble.bubble.repository.FileRepository;
import me.bubble.bubble.repository.WorkspaceRepository;
import me.bubble.bubble.util.SecurityUtil;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final FileRepository fileRepository;
    private final WorkspaceService workspaceService;
    private final BubbleRepository bubbleRepository;

    @Transactional
    public TempFileResponse uploadTempFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new FileNotSupportedException("EMPTY FILE");
        }
        String fileName = createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        String filePath = "/temp/" + fileName;
        try(InputStream inputStream = multipartFile.getInputStream()){
            amazonS3.putObject(new PutObjectRequest(bucket, filePath, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            File file = new File(0,0,0,0,filePath,false, false,0,null);
            File savedFile = fileRepository.save(file);
            return new TempFileResponse(savedFile.getId());
        } catch (IOException e){
            throw new FileUploadFailedException("FILE UPLOAD FAILED");
        }
    }

    public FileDataResponse getFileById(Long fileId) {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException("FILE NOT FOUND"));

        if (file.getBubble() == null) {
            throw new TempFileAccessedException("TEMP FILE CANT BE ACCESSED");
        }

        String workspaceOAuthId = workspaceService.getOAuthIdByWorkspaceId(file.getBubble().getWorkspace().getId());

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspaceOAuthId)) {
            throw new InappropriateUserException("Inappropriate User");
        }

        String filePath = file.getPath();

        try (S3Object s3Object = amazonS3.getObject(bucket, filePath)) {
            // 파일 데이터를 읽기
            InputStream inputStream = s3Object.getObjectContent();
            byte[] fileBytes = IOUtils.toByteArray(inputStream);

            return new FileDataResponse(fileBytes);
        } catch (IOException e) {
            throw new FileDownloadFailedException("FILE DOWNLOAD FAILED");
        }
    }

    @Transactional
    public PostFileResponse postFile (UUID workspaceId, PostFileRequest request) {
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

        if (!currentPath.startsWith("/temp")) {
            throw new IllegalStateException("File is not in the temporary directory: " + currentPath);
        }

        String targetPath = currentPath.replace("/temp", "/" + userOAuthId + "/" + workspaceId);
        // S3에서 파일 이동
        try {
            moveFileInS3(currentPath, targetPath);
        } catch (Exception e) {
            throw new FileMoveException("Failed to move file on S3");
        }

        file.update(request.getTop(), request.getLeft(), request.getWidth(), request.getHeight(),
                targetPath, request.getIsFlippedX(), request.getIsFlippedY(), request.getAngle(), currentBubble );
        // 2. DB 내용 수정하기
        File savedFile = fileRepository.save(file);

        // 응답 반환
        return new PostFileResponse(savedFile, request.getPath());
    }

    public List<File> findFilesByBubble(Bubble bubble) {
        return fileRepository.findByBubbleId(bubble.getId());
    }

    public PostFileResponse getFileInfoById(UUID workspaceId, Long fileId) {
        String workspaceOAuthId = workspaceService.getOAuthIdByWorkspaceId(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspaceOAuthId)) {
            throw new InappropriateUserException("Inappropriate User");
        }

        File file = fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException("FILE NOT FOUND"));

        return new PostFileResponse(file, file.getBubble().getPath());
    }


    private void moveFileInS3(String sourcePath, String targetPath) {
        // Copy the file to the new location
        amazonS3.copyObject(bucket, sourcePath, bucket, targetPath);

        // Delete the original file
        amazonS3.deleteObject(bucket, sourcePath);
    }

    // 파일명을 난수화하기 위해 UUID 를 활용하여 난수를 돌린다.
    public String createFileName(String fileName){
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    //  "."의 존재 유무만 판단
    private String getFileExtension(String fileName){
        try{
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일" + fileName + ") 입니다.");
        }
    }

    public void deleteFileById(UUID workspaceId, Long fileId) {
        String workspaceOAuthId = workspaceService.getOAuthIdByWorkspaceId(workspaceId);

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspaceOAuthId)) {
            throw new InappropriateUserException("Inappropriate User");
        }

        File file = fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException("FILE NOT FOUND"));

        String s3_path = file.getPath();
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, s3_path));

        fileRepository.delete(file);
    }
}
