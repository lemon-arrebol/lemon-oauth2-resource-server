package com.lemon.oauth2.custom.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

/**
 * @author lemon
 * @version 1.0
 * @description: TODO
 * @date Create by lemon on 2020-05-07 21:28
 */
@Slf4j
@Primary
@Component("customResourceServerTokenServices")
@ConditionalOnProperty(name = "lemon.oauth2.token.format", havingValue = "jwt")
public class CustomJwtResourceServerTokenServices implements ResourceServerTokenServices, InitializingBean {
    @Autowired
    @Qualifier("customTokenStore")
    private TokenStore tokenStore;

    private DefaultTokenServices tokenServices;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.tokenServices = new DefaultTokenServices();
        this.tokenServices.setTokenStore(this.tokenStore);

        log.info("初始化自定义资源服务JWT Token验证服务类 {}", CustomJwtResourceServerTokenServices.class);
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException, InvalidTokenException {
        return this.tokenServices.loadAuthentication(accessTokenValue);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        return this.tokenServices.readAccessToken(accessToken);
    }
}
