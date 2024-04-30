package com.cranker.cranker.unit.cloud;

import com.cranker.cranker.cloud.DataBucketUtilHelper;
import com.cranker.cranker.exception.APIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class DataBucketUtilHelperUnitTest {

    @InjectMocks
    private DataBucketUtilHelper dataBucketUtilHelper;

    @Test
    void shouldThrowAPIExceptionWhenProvidedFileNameHasUnsupportedExtension() {
        String filename = "test.pdf";

        assertThrows(APIException.class, () -> dataBucketUtilHelper.checkFileExtension(filename));
    }

    @Test
    void shouldPassValidationWhenProvidedFileNameHasValidExtension() {
        String filename = "test.png";

        assertDoesNotThrow(() -> dataBucketUtilHelper.checkFileExtension(filename));
    }
}
