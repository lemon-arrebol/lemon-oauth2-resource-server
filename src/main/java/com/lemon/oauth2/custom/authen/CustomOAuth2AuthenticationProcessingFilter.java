package com.lemon.oauth2.custom.authen;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetailsSource;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lemon
 * @version 1.0
 * @description: 使用@Component后会自动加入到过滤器中自定义认证处理过滤器
 * 参考 {@link org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter}
 * @date @link Create by lemon on 2020-05-06 21:11
 */
@Slf4j
@Data
//@Component("customOAuth2AuthenticationProcessingFilter")
public class CustomOAuth2AuthenticationProcessingFilter implements Filter, InitializingBean {
    private AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new OAuth2AuthenticationDetailsSource();
    private TokenExtractor tokenExtractor = new BearerTokenExtractor();
    private AuthenticationEventPublisher eventPublisher = new CustomOAuth2AuthenticationProcessingFilter.NullEventPublisher();
    private boolean stateless = true;

    @Autowired
    @Qualifier("customOAuth2AuthenticationManager")
    private AuthenticationManager authenticationManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(this.authenticationManager != null, "AuthenticationManager is required");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        boolean debug = log.isDebugEnabled();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            Authentication authentication = this.tokenExtractor.extract(request);

            if (authentication == null) {
                if (this.stateless && this.isAuthenticated()) {
                    if (debug) {
                        log.debug("Clearing security context.");
                    }

                    SecurityContextHolder.clearContext();
                }

                if (debug) {
                    log.debug("No token in request, will continue chain.");
                }
            } else {
                request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, authentication.getPrincipal());

                if (authentication instanceof AbstractAuthenticationToken) {
                    AbstractAuthenticationToken needsDetails = (AbstractAuthenticationToken) authentication;
                    needsDetails.setDetails(this.authenticationDetailsSource.buildDetails(request));
                }

                Authentication authResult = this.authenticationManager.authenticate(authentication);

                if (debug) {
                    log.debug("Authentication success: " + authResult);
                }

                this.eventPublisher.publishAuthenticationSuccess(authResult);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            }
        } catch (OAuth2Exception e) {
            SecurityContextHolder.clearContext();

            if (debug) {
                log.debug("Authentication request failed: " + e);
            }

            this.eventPublisher.publishAuthenticationFailure(new BadCredentialsException(e.getMessage(), e), new PreAuthenticatedAuthenticationToken("access-token", "N/A"));
            this.authenticationEntryPoint.commence(request, response, new InsufficientAuthenticationException(e.getMessage(), e));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }

    @Override
    public void destroy() {

    }

    private static final class NullEventPublisher implements AuthenticationEventPublisher {
        private NullEventPublisher() {
        }

        @Override
        public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        }

        @Override
        public void publishAuthenticationSuccess(Authentication authentication) {
        }
    }
}
