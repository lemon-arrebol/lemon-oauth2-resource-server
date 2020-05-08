package com.lemon.oauth2.custom.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

/**
 * @author lemon
 * @version 1.0
 * @description: 参考 {@link org.springframework.security.oauth2.provider.token.RemoteTokenServices}
 * {@link org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager} 使用 ResourceServerTokenServices 查询 OAuth2Authentication
 * @date Create by lemon on 2020-05-07 10:45
 */
@Slf4j
@Primary
@Component("customResourceServerTokenServices")
@ConditionalOnProperty(name = "lemon.oauth2.token.format", havingValue = "default", matchIfMissing = true)
public class CustomResourceServerTokenServices implements ResourceServerTokenServices, InitializingBean {
    @Value("${lemon.oauth2.resource.token.name:token}")
    private String tokenName;

    @Autowired
    private RestOperations restTemplate;

    @Autowired
    private ResourceServerProperties resourceServerProperties;

    @Autowired
    @Qualifier("customAccessTokenConverter")
    private AccessTokenConverter tokenConverter;

    private RemoteTokenServices remoteTokenServices;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.remoteTokenServices = new RemoteTokenServices();
        this.remoteTokenServices.setTokenName(this.tokenName);
        this.remoteTokenServices.setRestTemplate(this.restTemplate);
        this.remoteTokenServices.setAccessTokenConverter(this.tokenConverter);
        this.remoteTokenServices.setCheckTokenEndpointUrl(this.resourceServerProperties.getTokenInfoUri());
        this.remoteTokenServices.setClientId(this.resourceServerProperties.getClientId());
        this.remoteTokenServices.setClientSecret(this.resourceServerProperties.getClientSecret());

        log.info("初始化自定义资源服务Token验证服务类 {}", CustomResourceServerTokenServices.class);
    }

    /**
     * @param accessToken
     * @return org.springframework.security.oauth2.provider.OAuth2Authentication
     * @description 根据Token获取认证信息
     * @author lemon
     * @date 2020-05-07 18:14
     */
    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        return this.remoteTokenServices.loadAuthentication(accessToken);
    }

    /**
     * @param accessToken
     * @return org.springframework.security.oauth2.common.OAuth2AccessToken
     * @description 根据Token获取Token信息
     * @author lemon
     * @date 2020-05-07 18:14
     */
    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        return this.remoteTokenServices.readAccessToken(accessToken);
    }
}
