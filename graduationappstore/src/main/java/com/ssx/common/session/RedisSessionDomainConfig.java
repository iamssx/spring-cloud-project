package com.ssx.common.session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * 二级域名共享session
 */
@Configuration
public class RedisSessionDomainConfig {

     @Bean
    public RedisHttpSessionConfiguration springSessionDefaultRedisSerializer(RedisHttpSessionConfiguration redisHttpSessionConfiguration) {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
//        cookieSerializer.setDomainName(".ssx.com");
        cookieSerializer.setDomainNamePattern("^.+?\\.(ssx\\.com)$");
        cookieSerializer.setCookieName("JSESSIONID");
        cookieSerializer.setCookiePath("/");
        redisHttpSessionConfiguration.setCookieSerializer(cookieSerializer);
        return redisHttpSessionConfiguration;
    }
}
