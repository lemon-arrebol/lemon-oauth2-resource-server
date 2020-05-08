package com.lemon.oauth2.config;

import com.lemon.oauth2.custom.exception.CustomAuthExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.client.RestTemplate;

/**
 * @author lemon
 * @description OAuth2服务器配置
 * 资源服务器通过 @EnableResourceServer 注解来开启一个 {@link org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter} 类型的过滤器
 * @date 2020-05-01 21:29
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    public static final String ROLE_ADMIN = "ADMIN";

    @Value("${lemon.oauth2.resource.id:lemon-oauth2-resource}")
    private String resourceId;

    @Autowired
    @Qualifier("customResourceServerTokenServices")
    private ResourceServerTokenServices resourceServerTokenServices;

    @Autowired
    @Qualifier("customOAuth2AuthenticationManager")
    private AuthenticationManager authenticationManager;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private CustomAuthExceptionHandler customAuthExceptionHandler;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources
                .stateless(false)
                .resourceId(this.resourceId)
                .accessDeniedHandler(this.customAuthExceptionHandler)
                .authenticationEntryPoint(this.customAuthExceptionHandler)
                .tokenServices(this.resourceServerTokenServices)
                .authenticationManager(this.authenticationManager);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                // 请求权限配置
                .authorizeRequests()
                // OPTIONS请求不需要鉴权
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 用户的增删改接口只允许管理员访问
                .antMatchers(HttpMethod.POST, "/auth/user").hasAnyAuthority(ROLE_ADMIN)
                .antMatchers(HttpMethod.PUT, "/auth/user").hasAnyAuthority(ROLE_ADMIN)
                .antMatchers(HttpMethod.DELETE, "/auth/user").hasAnyAuthority(ROLE_ADMIN)
                // 获取角色 权限列表接口只允许系统管理员及高级用户访问
                .antMatchers(HttpMethod.GET, "/auth/role").hasAnyAuthority(ROLE_ADMIN)
                // 其余接口没有角色限制，但需要经过认证，只要携带token就可以放行
                .anyRequest()
                .authenticated();

    }
}

