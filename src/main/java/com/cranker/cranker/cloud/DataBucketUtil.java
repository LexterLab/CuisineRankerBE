package com.cranker.cranker.cloud;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.utils.Messages;
import com.cranker.cranker.utils.Properties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class DataBucketUtil {
    private final Logger logger = LogManager.getLogger(this);
    private final Properties properties;
    private Storage storage;
    private final DataBucketUtilHelper helper;

    @PostConstruct
    public void init() throws IOException {
        storage = StorageOptions.newBuilder()
                .setProjectId(properties.getGcpProjectId())
                .setCredentials(GoogleCredentials
                        .fromStream((new ClassPathResource(properties.getGcpConfigFile()).getInputStream())))
                .build().getService();
    }

    public CloudFileDTO uploadFile(MultipartFile multipartFile, String email) {
        try {

            String fileName = helper.generateUniqueFileName(multipartFile.getOriginalFilename());

            helper.checkFileExtension(fileName);

            Blob blob = storage.get(properties.getGcpBucketId()).create(
                    fileName,
                    multipartFile.getBytes(),
                    multipartFile.getContentType()
            );

            logger.info("Uploading file {} to bucket {}", fileName, properties.getGcpBucketId());

            if (blob != null) {
                String publicUrl = "https://storage.googleapis.com/" + properties.getGcpBucketId() + "/" + fileName;
                return new CloudFileDTO(fileName, publicUrl, email);
            }

        } catch (Exception exception) {
            logger.error("Error while uploading file : {}", exception.getMessage());
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.UNSUCCESSFUL_GCS_OPERATION);
        }
        logger.error("Unsuccessful storing operation to GCS");
        throw new APIException(HttpStatus.BAD_REQUEST, Messages.UNSUCCESSFUL_GCS_OPERATION);
    }


}
