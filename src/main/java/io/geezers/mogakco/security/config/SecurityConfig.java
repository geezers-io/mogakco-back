package io.geezers.mogakco.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.geezers.mogakco.api.common.Endpoint;
import io.geezers.mogakco.security.filter.AjaxLoginProcessingFilter;
import io.geezers.mogakco.security.handler.AjaxAuthenticationFailureHandler;
import io.geezers.mogakco.security.handler.AjaxAuthenticationSuccessHandler;
import io.geezers.mogakco.security.provider.AjaxAuthenticationProvider;
import io.geezers.mogakco.web.dto.error.ErrorResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final String SESSION_ID = "SESSION";
    private final String[] DEFAULT_ACCESS_WHITELIST = {Endpoint.Api.LOGIN, "/h2-console/**"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.cors().configurationSource(corsConfigurationSource());
        http.authorizeRequests()
                .antMatchers(DEFAULT_ACCESS_WHITELIST).permitAll()
                .antMatchers(HttpMethod.POST, Endpoint.Api.USER).permitAll()
                .antMatchers(HttpMethod.GET, Endpoint.Api.AUTH).permitAll()
                .anyRequest().authenticated();
        http
                .formLogin().disable();
        http
                .logout()
                .logoutUrl(Endpoint.Api.LOGOUT)
                .logoutSuccessHandler(logoutSuccessHandler())
                .invalidateHttpSession(true)
                .deleteCookies(SESSION_ID);
        http
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler());
        ajaxConfigurer(http);
    }

    private CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return urlBasedCorsConfigurationSource;
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK);
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(), ErrorResponseDto.builder()
                    .message(accessDeniedException.getMessage())
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .build());
        };
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(response.getWriter(), ErrorResponseDto.builder()
                    .message(accessDeniedException.getMessage())
                    .status(HttpServletResponse.SC_FORBIDDEN)
                    .build());
        };
    }

    private void ajaxConfigurer(HttpSecurity http) throws Exception {
        http
                .apply(new AjaxLoginConfigurer<>(
                        new AjaxLoginProcessingFilter(Endpoint.Api.LOGIN, ajaxAuthenticationManager()),
                        Endpoint.Api.LOGIN,
                        ajaxAuthenticationManager()));
        http
                .addFilterAfter(ajaxLoginProcessingFilter(), SecurityContextPersistenceFilter.class);
    }

    private AuthenticationManager ajaxAuthenticationManager() {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        authenticationProviders.add(ajaxAuthenticationProvider());
        return new ProviderManager(authenticationProviders);
    }

    private AjaxLoginProcessingFilter ajaxLoginProcessingFilter() {
        AjaxLoginProcessingFilter ajaxLoginProcessingFilter = new AjaxLoginProcessingFilter(Endpoint.Api.LOGIN, ajaxAuthenticationManager());
        ajaxLoginProcessingFilter.setAuthenticationManager(ajaxAuthenticationManager());
        ajaxLoginProcessingFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        ajaxLoginProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        return ajaxLoginProcessingFilter;
    }

    @Bean
    public AjaxAuthenticationProvider ajaxAuthenticationProvider() {
        return new AjaxAuthenticationProvider(userDetailsService, passwordEncoder);
    }

    @Bean
    public AjaxAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AjaxAuthenticationSuccessHandler(objectMapper);
    }

    @Bean
    public AjaxAuthenticationFailureHandler authenticationFailureHandler() {
        return new AjaxAuthenticationFailureHandler(objectMapper);
    }
}
