package com.lemon.oauth2.config;

import com.lemon.oauth2.interceptor.WebAccessInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @param
 * @author lemon
 * @description
 * @return
 * @date 2019-08-18 20:11
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 可添加多个
        registry.addInterceptor(new WebAccessInterceptor());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // TODO 设置login不会覆盖默认值
//        registry.addViewController("/login").setViewName("forward:/base-login.html");
//        registry.addViewController("/oauth/confirm_acces").setViewName("forward:/base-approval.html");
//
//        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

}