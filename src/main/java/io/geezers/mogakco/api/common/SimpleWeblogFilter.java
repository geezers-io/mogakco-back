package io.geezers.mogakco.api.common;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SimpleWeblogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        double startTimeNano = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Unhandled Error", e);
        } finally {
            double endTimeNano = System.nanoTime();
            double processingTimeNano = endTimeNano - startTimeNano;
            double processingTimeMillis = processingTimeNano / 1000 / 1000;
            double processingTimeMillisRoundedToThreeDecimalPlaces = ((double) Math.round(processingTimeMillis) * 1000) / 1000;

            String method = request.getMethod();
            String requestURI = request.getRequestURI();
            int status = response.getStatus();

            log.info("{} {} {} - {} ms", method, requestURI, status, processingTimeMillisRoundedToThreeDecimalPlaces);
        }
    }

    @Override
    public void destroy() {}
}
