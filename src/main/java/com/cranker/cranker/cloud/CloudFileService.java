package com.cranker.cranker.cloud;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
import com.cranker.cranker.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CloudFileService {
    private final Logger logger = LogManager.getLogger(this);
    private final DataBucketUtil dataBucketUtil;
    private final UserRepository userRepository;

    @Transactional
    public CloudFileDTO uploadFileToCloud(MultipartFile file, String email) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("user", "email", email));

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isEmpty()) {
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.EMPTY_FILE_NAME);
        }

        logger.info("Uploaded file: {}  by: {}", fileName, user.getEmail());
        return dataBucketUtil.uploadFile(file, email);
    }
}
