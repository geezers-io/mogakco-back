package io.geezers.mogakco.security.config;

import io.geezers.mogakco.security.filter.AjaxLoginProcessingFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class AjaxLoginConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractAuthenticationFilterConfigurer<H, AjaxLoginConfigurer<H>, AjaxLoginProcessingFilter> {
    private AuthenticationManager authenticationManager;

    public AjaxLoginConfigurer(AjaxLoginProcessingFilter authenticationFilter, String defaultLoginProcessingUrl, AuthenticationManager authenticationManager) {
        super(authenticationFilter, defaultLoginProcessingUrl);
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, HttpMethod.POST.name());
    }

    @Override
    public void configure(H http) {
        if (authenticationManager == null) {
            authenticationManager = http.getSharedObject(AuthenticationManager.class);
        }

        SessionAuthenticationStrategy sessionAuthenticationStrategy = http.getSharedObject(
                SessionAuthenticationStrategy.class);

        if (sessionAuthenticationStrategy != null) {
            getAuthenticationFilter().setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }

        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);

        if (rememberMeServices != null) {
            getAuthenticationFilter().setRememberMeServices(rememberMeServices);
        }

        http.setSharedObject(AjaxLoginProcessingFilter.class, getAuthenticationFilter());
    }
}
