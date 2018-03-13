package com.ssx.client;

import com.ssx.common.domain.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("user-service")
public interface UserService {

    @RequestMapping(value = "/user/currentUser", method = RequestMethod.GET)
    User getUser(@RequestHeader("Cookie") String cookies);

}