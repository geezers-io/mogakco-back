package io.geezers.mogakco.api.common;

import io.geezers.mogakco.web.dto.error.ErrorResponseDto;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;

import static io.geezers.mogakco.api.util.ApiErrorResponseUtil.getErrorResponseDtoResponseEntity;

@Order
@RestControllerAdvice
public class CommonApiControllerAdvice {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(NoHandlerFoundException exception) {
        return getErrorResponseDtoResponseEntity(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleRequestMethodNotSupportedException(Exception methodArgumentNotValidException) {
        return getErrorResponseDtoResponseEntity(HttpServletResponse.SC_METHOD_NOT_ALLOWED, methodArgumentNotValidException.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleException(Exception exception) {
        return getErrorResponseDtoResponseEntity(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
