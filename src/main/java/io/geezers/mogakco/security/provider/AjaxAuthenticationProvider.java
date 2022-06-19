package io.geezers.mogakco.security.provider;

import io.geezers.mogakco.domain.dto.user.UserAuthenticationDto;
import io.geezers.mogakco.domain.dto.user.UserLoginRequestDto;
import io.geezers.mogakco.security.token.AjaxAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class AjaxAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserLoginRequestDto principal = (UserLoginRequestDto) authentication.getPrincipal();

        final String email = principal.getEmail();
        final String password = principal.getPassword();

        UserAuthenticationDto userDetails = (UserAuthenticationDto) userDetailsService.loadUserByUsername(email);

        if (!validatePassword(userDetails, password)) {
            throw new BadCredentialsException("Password is invalid.");
        }

        if (!userDetails.isEnabled()) {
            throw new BadCredentialsException("This account is disabled.");
        }

        return AjaxAuthenticationToken.of(userDetails);
    }

    private boolean validatePassword(UserAuthenticationDto userDetails, String password) {
        return userDetails != null && passwordEncoder.matches(password, userDetails.getPassword());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(AjaxAuthenticationToken.class);
    }
}
