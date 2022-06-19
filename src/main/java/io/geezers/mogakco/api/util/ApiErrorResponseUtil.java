package io.geezers.mogakco.api.util;

import io.geezers.mogakco.web.dto.error.ErrorResponseDto;
import org.springframework.http.ResponseEntity;

public class ApiErrorResponseUtil {
    public static ResponseEntity<ErrorResponseDto> getErrorResponseDtoResponseEntity(int status, String msg) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponseDto.builder()
                        .status(status)
                        .message(msg)
                        .build());
    }
}
