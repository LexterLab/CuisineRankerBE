package com.cranker.cranker.cloud;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.utils.Messages;
import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataBucketUtilHelper {
    private final Logger logger = LogManager.getLogger(this);

    public void checkFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            String[] extensionList = {".png", ".jpg", ".jpeg", ".gif", ".bmp", ".mp3"};
            boolean validExtension = false;
            for (String extension : extensionList) {
                if (fileName.toLowerCase().endsWith(extension)) {
                    validExtension = true;
                    break;
                }
            }

            if (!validExtension) {
                logger.error(Messages.UNSUPPORTED_EXTENSION);
                throw new APIException(HttpStatus.BAD_REQUEST, Messages.UNSUPPORTED_EXTENSION);
            }
        }
    }

    public String generateUniqueFileName(String originalFileName) {
        return UUID.randomUUID() + Files.getFileExtension(originalFileName);
    }
}
