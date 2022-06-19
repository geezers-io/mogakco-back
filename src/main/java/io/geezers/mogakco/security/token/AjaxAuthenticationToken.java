package io.geezers.mogakco.security.token;

import io.geezers.mogakco.domain.dto.user.UserAuthenticationDto;
import io.geezers.mogakco.domain.dto.user.UserLoginRequestDto;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AjaxAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private final Object credentials;

    private AjaxAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    private AjaxAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal, Object credentials) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    public static AjaxAuthenticationToken of(UserLoginRequestDto requestDto) {
        return new AjaxAuthenticationToken(requestDto, requestDto.getPassword());
    }

    public static AjaxAuthenticationToken of(UserAuthenticationDto userAuthenticationDto) {
        return new AjaxAuthenticationToken(userAuthenticationDto.getAuthorities(), userAuthenticationDto, userAuthenticationDto.getPassword());
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
