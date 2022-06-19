package io.geezers.mogakco.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.geezers.mogakco.domain.dto.user.UserLoginResponseDto;
import io.geezers.mogakco.domain.dto.user.UserAuthenticationDto;
import io.geezers.mogakco.domain.dto.user.Me;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        UserAuthenticationDto userAuthenticationDto = (UserAuthenticationDto) authentication.getPrincipal();
        objectMapper.writeValue(response.getWriter(), UserLoginResponseDto.of(Me.builder()
                .id(userAuthenticationDto.getId())
                .email(userAuthenticationDto.getUsername())
                .build()));
    }
}
