package io.geezers.mogakco.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.geezers.mogakco.web.dto.error.ErrorResponseDto;
import io.geezers.mogakco.security.exception.AjaxMethodNotSupportedException;
import io.geezers.mogakco.security.exception.LoginArgumentNotValidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class AjaxAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (exception instanceof LoginArgumentNotValidException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else if (exception instanceof AjaxMethodNotSupportedException) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .status(response.getStatus())
                .message(exception.getMessage())
                .build();

        objectMapper.writeValue(response.getWriter(), errorResponseDto);
    }
}
