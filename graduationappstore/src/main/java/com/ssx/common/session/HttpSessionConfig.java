package com.ssx.common.session;

import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 开启redis保存session
 */
@EnableRedisHttpSession
public class HttpSessionConfig {
}
