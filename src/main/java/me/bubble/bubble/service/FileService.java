package me.bubble.bubble.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import me.bubble.bubble.domain.Bubble;
import me.bubble.bubble.domain.File;
import me.bubble.bubble.domain.Picture;
import me.bubble.bubble.dto.request.PostPictureRequest;
import me.bubble.bubble.dto.response.FileDataResponse;
import me.bubble.bubble.dto.response.PictureResponse;
import me.bubble.bubble.dto.response.TempFileResponse;
import me.bubble.bubble.exception.*;
import me.bubble.bubble.repository.BubbleRepository;
import me.bubble.bubble.repository.FileRepository;
import me.bubble.bubble.repository.PictureRepository;
import me.bubble.bubble.util.SecurityUtil;
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
    private final PictureRepository pictureRepository;

    @Transactional
    public TempFileResponse uploadTempFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new FileNotSupportedException("EMPTY FILE");
        }

        String fileName = createFileName(multipartFile.getOriginalFilename());

        Long sizeInBytes = multipartFile.getSize();
        Double sizeInMB = (sizeInBytes / (1024.0 * 1024.0));  // MB 단위 변환

        String contentType = multipartFile.getContentType();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(sizeInBytes);
        objectMetadata.setContentType(contentType);
        String filePath = "/temp/" + fileName;
        try(InputStream inputStream = multipartFile.getInputStream()){
            amazonS3.putObject(new PutObjectRequest(bucket, filePath, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            File file = new File(filePath, contentType, sizeInMB);
            File savedFile = fileRepository.save(file);
            return new TempFileResponse(savedFile.getId());
        } catch (IOException e){
            throw new FileUploadFailedException("FILE UPLOAD FAILED");
        }
    }

    public FileDataResponse getFileById(Long fileId) {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException("FILE NOT FOUND"));

        String filePath = file.getPath();
        if (filePath.startsWith("/temp/")) {
            throw new TempFileAccessedException("TEMP FILE CAN'T BE ACCESSED");
        }

        Picture picture = pictureRepository.findByFileId(fileId).orElseThrow(() -> new PictureNotFoundException("PICTURE NOT FOUND"));

        String workspaceOAuthId = workspaceService.getOAuthIdByWorkspaceId(picture.getBubble().getWorkspace().getId());

        String userOAuthId = SecurityUtil.getCurrentUserOAuthId();
        if (!userOAuthId.equals(workspaceOAuthId)) {
            throw new InappropriateUserException("Inappropriate User");
        }

        try (S3Object s3Object = amazonS3.getObject(bucket, filePath)) {
            // 파일 데이터를 읽기
            InputStream inputStream = s3Object.getObjectContent();
            byte[] fileBytes = IOUtils.toByteArray(inputStream);

            return new FileDataResponse(fileBytes);
        } catch (IOException e) {
            throw new FileDownloadFailedException("FILE DOWNLOAD FAILED");
        }
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
}
