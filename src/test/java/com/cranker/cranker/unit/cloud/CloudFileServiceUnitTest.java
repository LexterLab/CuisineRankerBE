package com.cranker.cranker.unit.cloud;

import com.cranker.cranker.cloud.CloudFileDTO;
import com.cranker.cranker.cloud.CloudFileService;
import com.cranker.cranker.cloud.DataBucketUtil;
import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CloudFileServiceUnitTest {
    @InjectMocks
    private CloudFileService cloudFileService;
    @Mock
    private DataBucketUtil dataBucketUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MultipartFile multipartFile;

    @Test
    void shouldUploadFile() {
        String email = "user@gmail.com";
        User user = new User();
        user.setEmail(email);

        String fileName = "file.png";
        CloudFileDTO expectedFileData = new CloudFileDTO(fileName, "url", email);

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(dataBucketUtil.uploadFile(multipartFile, email)).thenReturn(expectedFileData);

        CloudFileDTO cloudFileDTO = cloudFileService.uploadFileToCloud(multipartFile, email);

        assertEquals(expectedFileData, cloudFileDTO);
    }

    @Test
    void shouldThrowAPIExceptionWhenProvidedMissingFileName() {
        String email = "user@gmail.com";
        User user = new User();
        user.setEmail(email);

        String fileName = "";

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);

        assertThrows(APIException.class, () -> cloudFileService.uploadFileToCloud(multipartFile, email));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenProvidedUnexistingUserEmail() {
        String email = "unexisting@gmail.com";

        when(userRepository.findUserByEmailIgnoreCase(email)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> cloudFileService.uploadFileToCloud(multipartFile, email));
    }
}
