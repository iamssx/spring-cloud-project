package com.feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Collection;
import java.util.Map;

//@Configuration
public class MyFeignClientsConfiguration {

//    @Bean
    public RequestInterceptor headerInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                Map<String, Collection<String>> headers = requestTemplate.headers();
                String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
                requestTemplate.header("Cookie", "JSESSIONID=" + sessionId);
            }
        };
    }
}
