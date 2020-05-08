package com.lemon.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author lemon
 * @description
 * @date 2020-05-06 17:06
 */
@Slf4j
@EnableOAuth2Sso
// 显示禁用DataSource
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class LemonOAuth2ResourceApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(LemonOAuth2ResourceApplication.class, args);
        Environment env = configurableApplicationContext.getEnvironment();

        log.info("\n----------------------------------------------------------\n\tOAuth2资源服务 '{}' 启动完成! \n\t端口号(s): \t{}\n\t环境(s): \t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"), env.getProperty("server.port"), env.getActiveProfiles());
    }

}
