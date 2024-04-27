package com.cranker.cranker.unit.cloud;

import com.cranker.cranker.cloud.CloudFileController;
import com.cranker.cranker.cloud.CloudFileDTO;
import com.cranker.cranker.cloud.CloudFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CloudFileControllerUnitTest {

    @InjectMocks
    private CloudFileController cloudFileController;
    @Mock
    private CloudFileService cloudFileService;
    @Mock
    private MultipartFile multipartFile;
    @Mock
    private Authentication authentication;

    @Test
    void shouldRespondWithFileDataAndCreatedWhenUploadingFile() {
        String userEmail = "test@test.com";
        String fileName = "file.png";
        CloudFileDTO expectedFileData =  new CloudFileDTO(fileName, "url", userEmail);

        when(authentication.getName()).thenReturn(userEmail);
        when(cloudFileService.uploadFileToCloud(multipartFile, userEmail)).thenReturn(expectedFileData);

        ResponseEntity<CloudFileDTO> response = cloudFileController.uploadFile(multipartFile, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedFileData, response.getBody());
    }
}
