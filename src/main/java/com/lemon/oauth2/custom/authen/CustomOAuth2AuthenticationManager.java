package com.lemon.oauth2.custom.authen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * @author lemon
 * @version 1.0
 * @description: 参考 {@link org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager}
 * {@link org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter} 使用 AuthenticationManager 验证 Authentication
 * @date Create by lemon on 2020-05-07 10:23
 */
@Slf4j
@Component("customOAuth2AuthenticationManager")
public class CustomOAuth2AuthenticationManager implements AuthenticationManager, InitializingBean {
    @Value("${lemon.oauth2.resource.id:lemon-oauth2-resource}")
    private String resourceId;

    @Autowired
    @Qualifier("customResourceServerTokenServices")
    private ResourceServerTokenServices tokenServices;

    @Autowired(required = false)
    private ClientDetailsService clientDetailsService;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(this.tokenServices != null, "TokenServices are required");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication == null) {
            throw new InvalidTokenException("Invalid token (token not found)");
        } else {
            String token = (String) authentication.getPrincipal();
            OAuth2Authentication authen = this.tokenServices.loadAuthentication(token);

            if (authen == null) {
                throw new InvalidTokenException("Invalid token: " + token);
            } else {
                // client允许访问的资源服务
                Collection<String> resourceIds = authen.getOAuth2Request().getResourceIds();

                if (this.resourceId != null && resourceIds != null && !resourceIds.isEmpty() && !resourceIds.contains(this.resourceId)) {
                    throw new OAuth2AccessDeniedException("Invalid token does not contain resource id (" + this.resourceId + ")");
                } else {
                    this.checkClientDetails(authen);

                    if (authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
                        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();

                        if (!details.equals(authen.getDetails())) {
                            details.setDecodedDetails(authen.getDetails());
                        }
                    }

                    authen.setDetails(authentication.getDetails());
                    authen.setAuthenticated(true);
                    return authen;
                }
            }
        }
    }

    /**
     * @param auth
     * @return void
     * @description
     * @author lemon
     * @date 2020-05-07 10:27
     */
    private void checkClientDetails(OAuth2Authentication auth) {
        if (this.clientDetailsService != null) {
            ClientDetails client;

            try {
                client = this.clientDetailsService.loadClientByClientId(auth.getOAuth2Request().getClientId());
            } catch (ClientRegistrationException var6) {
                throw new OAuth2AccessDeniedException("Invalid token contains invalid client id");
            }

            // 允许访问的scope
            Set<String> allowed = client.getScope();
            // 请求访问的scope
            Iterator scopeIterator = auth.getOAuth2Request().getScope().iterator();

            while (scopeIterator.hasNext()) {
                String scope = (String) scopeIterator.next();

                if (!allowed.contains(scope)) {
                    throw new OAuth2AccessDeniedException("Invalid token contains disallowed scope (" + scope + ") for this client");
                }
            }
        }

    }
}
