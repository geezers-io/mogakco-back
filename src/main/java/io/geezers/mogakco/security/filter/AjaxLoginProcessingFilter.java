package io.geezers.mogakco.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.geezers.mogakco.domain.dto.user.UserLoginRequestDto;
import io.geezers.mogakco.security.exception.LoginArgumentNotValidException;
import io.geezers.mogakco.security.exception.AjaxMethodNotSupportedException;
import io.geezers.mogakco.security.token.AjaxAuthenticationToken;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    public AjaxLoginProcessingFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        if (isSupportAjax(request)) {
            throw new AjaxMethodNotSupportedException(String.format("%s method is not for login. Please use POST with login information.", request.getMethod()));
        }
        ObjectMapper objectMapper = new ObjectMapper();

        UserLoginRequestDto requestDto = objectMapper.readValue(request.getReader(), UserLoginRequestDto.class);

        if (isBlankLoginRequestDto(requestDto)) {
            throw new LoginArgumentNotValidException("Email or Password is require.");
        }

        AjaxAuthenticationToken ajaxAuthenticationToken = AjaxAuthenticationToken.of(requestDto);
        return getAuthenticationManager().authenticate(ajaxAuthenticationToken);
    }

    private boolean isSupportAjax(HttpServletRequest request) {
        return !request.getMethod().equals(HttpMethod.POST.name()) || !isAjax(request);
    }

    private boolean isBlankLoginRequestDto(UserLoginRequestDto requestDto) {
        return !StringUtils.hasText(requestDto.getEmail()) || !StringUtils.hasText(requestDto.getPassword());
    }

    private boolean isAjax(HttpServletRequest request) {
        return request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE);
    }
}
