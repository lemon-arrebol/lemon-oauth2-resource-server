package com.lemon.oauth2.custom.authen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author lemon
 * @description 自定义 Authentication 转换器，认证信息编码、解码
 * {@link org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter#convertAccessToken} 转换 AccessToke 时放入用户信息
 * @date 2020-05-02 21:03
 */
@Slf4j
@Component("customUserAuthenticationConverter")
public class CustomUserAuthenticationConverter implements UserAuthenticationConverter, InitializingBean {
    @Value("${lemon.oauth2.authentConverter.defaultAuthorities:}")
    private String[] defaultAuthorities;

    private DefaultUserAuthenticationConverter userAuthenticationConverter;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.userAuthenticationConverter = new DefaultUserAuthenticationConverter();
        this.userAuthenticationConverter.setDefaultAuthorities(this.defaultAuthorities);
        log.info("初始化自定义Authentication转换器类 {}", CustomUserAuthenticationConverter.class);
    }

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        throw new UnsupportedOperationException("No convert Authentication to Map ");
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        return this.userAuthenticationConverter.extractAuthentication(map);
    }
}
