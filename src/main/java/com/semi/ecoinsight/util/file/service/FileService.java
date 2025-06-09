package com.semi.ecoinsight.util.file.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import com.semi.ecoinsight.exception.util.FileStreamException;
import com.semi.ecoinsight.exception.util.FileTypeNotAllowedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;


    @SuppressWarnings("null")
    public String store(MultipartFile file) {

        // 빈파일명 체크
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            throw new FileStreamException("파일명이 비어 있습니다.");
        }
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // 이미지 형식 체크
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileTypeNotAllowedException("이미지 파일만 업로드 가능합니다.");
        }

        // 확장자 추출 
        String extension = StringUtils.getFilenameExtension(originalFileName);
        if (extension == null) {
            throw new FileStreamException("확장자 추출에 실패했습니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(
                DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String newFileName = timestamp + "." + extension;

        PutObjectRequest request 
            = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(newFileName)
                        .contentType(file.getContentType())
                        .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + newFileName;
        } catch (IOException e) {
            throw new FileStreamException("파일 저장중 오류 발생");
        }
    }



    public void deleteFile(String fileUrl) {
        // Path filePath = fileLocation.resolve(fileUrl.substring(24)).normalize();
        
        try {
            URL url = new URI(fileUrl).toURL();
            log.info(url.getPath());
            String path = url.getPath().substring(1);

            DeleteObjectRequest deleteRequest =
                DeleteObjectRequest.builder()
                                .bucket(bucketName)
                                .key(path)
                                .build();

            s3Client.deleteObject(deleteRequest);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // try {
        //     Files.deleteIfExists(filePath);
        // } catch (IOException e) {
        //     throw new FileStreamException("파일 삭제중 오류 발생: " + e.getMessage());
        // }

        
    }
}
