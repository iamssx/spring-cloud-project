package com.ssx.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource("config/weiboAuth.properties")
@Component
@ConfigurationProperties
public class WeiboConfig {

}
