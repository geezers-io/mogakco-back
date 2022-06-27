package io.geezers.mogakco.api.user;

import io.geezers.mogakco.exception.AlreadyUserExistingException;
import io.geezers.mogakco.web.dto.error.ErrorResponseDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

import static io.geezers.mogakco.api.util.ApiErrorResponseUtil.getErrorResponseDtoResponseEntity;

@Order(Ordered.LOWEST_PRECEDENCE - 1)
@RestControllerAdvice(assignableTypes = UserApiController.class)
public class UserApiControllerAdvice {

    @ExceptionHandler(AlreadyUserExistingException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyUserExistingException(
            AlreadyUserExistingException alreadyUserExistingException) {
        return getErrorResponseDtoResponseEntity(
                HttpServletResponse.SC_CONFLICT,
                alreadyUserExistingException.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(Exception methodArgumentNotValidException) {
        return getErrorResponseDtoResponseEntity(
                HttpServletResponse.SC_BAD_REQUEST,
                methodArgumentNotValidException.getMessage());
    }
}
