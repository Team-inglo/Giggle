package com.inglo.giggle.utility;

import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3Client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Component
public class ImageUtil {
    private final String IMAGE_CONTENT_PREFIX = "image/";


    @Value("${spring.image.path}")
    private String resourcePath;

    @Autowired
    private AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Value("${cloud.aws.s3.url}")
    private String bucketUrl;

    public String encodeImageToBase64(MultipartFile file) {
        try {
            byte[] imageBytes = file.getBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String decodeAndUploadBase64(String base64Image) {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        String uuid = UUID.randomUUID().toString();

        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

            ImageIO.write(bufferedImage, "png", new File(resourcePath + uuid + ".png"));
        } catch (IOException e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

//        return serverUrl + "/images/" + uuid + ".png";
        return uuid + ".png";
    }

    public String uploadPassportImageFile(MultipartFile file, Long userId) {
        final String contentType = file.getContentType();
        assert contentType != null;
        String type = "." + contentType.substring(contentType.indexOf("/") + 1);

        if (!contentType.startsWith(IMAGE_CONTENT_PREFIX)) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_PARAMETER);
        }

        String uuid = UUID.randomUUID().toString();
        String fileName = "user_" + userId + "/passport/" + uuid + type;

        try {
            amazonS3Client.putObject(bucketName, fileName, file.getInputStream(), null);
        } catch (IOException e) {
            throw new CommonException(ErrorCode.UPLOAD_FILE_ERROR);
        }

        return bucketUrl + fileName;
    }

    public String uploadRegistrationImageFile(MultipartFile file, Long userId) {
        final String contentType = file.getContentType();
        assert contentType != null;
        String type = "." + contentType.substring(contentType.indexOf("/") + 1);

        if (!contentType.startsWith(IMAGE_CONTENT_PREFIX)) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_PARAMETER);
        }

        String uuid = UUID.randomUUID().toString();
        String fileName = "user_" + userId + "/registration/" + uuid + type;

        try {
            amazonS3Client.putObject(bucketName, fileName, file.getInputStream(), null);
        } catch (IOException e) {
            throw new CommonException(ErrorCode.UPLOAD_FILE_ERROR);
        }

        return bucketUrl + fileName;
    }
    public void deleteImageFile(String fileName) {
        amazonS3Client.deleteObject(bucketName, fileName);
    }
}
