//package com.lemon.oauth2.custom.authen;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.ApplicationEvent;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.security.authentication.AuthenticationDetailsSource;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.oauth2.client.OAuth2RestOperations;
//import org.springframework.security.oauth2.client.filter.OAuth2AuthenticationFailureEvent;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
//import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
//import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetailsSource;
//import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author lemon
// * @version 1.0
// * @description: Spring实例化后会自动加入到过滤器中没有用到 OAuth2ClientAuthenticationProcessingFilter
// * 拦截 filterProcessesUrl 指定的请求，根据authentication code获取token
// * 参考 {@link org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter}
// * @date Create by lemon on 2020-05-06 19:27
// */
//@Slf4j
//@Component("customOAuth2ClientAuthenticationProcessingFilter")
//public class CustomOAuth2ClientAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter implements InitializingBean, ApplicationContextAware {
//    @Autowired
//    @Qualifier("customOAuth2AuthenticationManager")
//    private AuthenticationManager authenticationManager;
//
//    /**
//     * 校验 Token
//     * DefaultUserInfoRestTemplateFactory实例了OAuth2RestTemplate
//     * DefaultUserInfoRestTemplateFactory主要是在ResourceServerTokenServicesConfiguration配置中创建的
//     * 这个是给resource server用的，因而client要使用的话，需要自己创建
//     */
//    @Autowired
//    @Qualifier("customResourceServerTokenServices")
//    private ResourceServerTokenServices tokenServices;
//
//    /**
//     * 获取 Token
//     * DefaultUserInfoRestTemplateFactory实例了OAuth2RestTemplate
//     * DefaultUserInfoRestTemplateFactory主要是在ResourceServerTokenServicesConfiguration配置中创建的
//     * 这个是给resource server用的，因而client要使用的话，需要自己创建
//     */
//    public OAuth2RestOperations restTemplate;
//    private ApplicationEventPublisher eventPublisher;
//    private ApplicationContext applicationContext;
//    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new OAuth2AuthenticationDetailsSource();
//
//    public CustomOAuth2ClientAuthenticationProcessingFilter(@Value("${lemon.oauth2.client.filterProcessesUrl:/**}") String filterProcessesUrl) {
//        super(new AntPathRequestMatcher(filterProcessesUrl));
////        this.setAuthenticationManager((authentication) -> {
////            throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
////        });
////        this.setAuthenticationDetailsSource(this.authenticationDetailsSource);
//    }
//
//    @Override
//    public void afterPropertiesSet() {
//        super.setAuthenticationManager(new CustomOAuth2ClientAuthenticationProcessingFilter.NoopAuthenticationManager());
//        this.restTemplate = this.applicationContext.getBean(UserInfoRestTemplateFactory.class).getUserInfoRestTemplate();
////        super.setAuthenticationManager(this.authenticationManager);
//        super.setAuthenticationDetailsSource(this.authenticationDetailsSource);
//
//        super.afterPropertiesSet();
//
//        log.info("初始化自定义OAuth2客户端认证服务类 {}", CustomOAuth2ClientAuthenticationProcessingFilter.class);
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
//        log.info("RequestURI is {}", httpServletRequest.getRequestURI());
//        OAuth2AccessToken accessToken;
//        BadCredentialsException bad;
//
//        try {
//            accessToken = this.restTemplate.getAccessToken();
//        } catch (OAuth2Exception var7) {
//            bad = new BadCredentialsException("Could not obtain access token", var7);
//            this.publish(new OAuth2AuthenticationFailureEvent(bad));
//            throw bad;
//        }
//
//        try {
//            OAuth2Authentication result = this.tokenServices.loadAuthentication(accessToken.getValue());
//
//            if (this.authenticationDetailsSource != null) {
//                httpServletRequest.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, accessToken.getValue());
//                httpServletRequest.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, accessToken.getTokenType());
//                result.setDetails(this.authenticationDetailsSource.buildDetails(httpServletRequest));
//            }
//
//            this.publish(new AuthenticationSuccessEvent(result));
//            return result;
//        } catch (InvalidTokenException var6) {
//            bad = new BadCredentialsException("Could not obtain user details from token", var6);
//            this.publish(new OAuth2AuthenticationFailureEvent(bad));
//            throw bad;
//        }
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.eventPublisher = applicationContext;
//        this.applicationContext = applicationContext;
//    }
//
//    private void publish(ApplicationEvent event) {
//        if (this.eventPublisher != null) {
//            this.eventPublisher.publishEvent(event);
//        }
//
//    }
//
//    private static class NoopAuthenticationManager implements AuthenticationManager {
//        private NoopAuthenticationManager() {
//        }
//
//        @Override
//        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//            throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
//        }
//    }
//}
