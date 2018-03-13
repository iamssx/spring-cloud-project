package com.ssx.resource.client;

import com.feign.MyFeignClientsConfiguration;
import com.ssx.common.domain.Result;
import com.ssx.common.domain.User;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service-v1", configuration = MyFeignClientsConfiguration.class)
public interface UserService {
//
//    @RequestMapping(value = "/user/islogin/{username}", method = RequestMethod.GET)
//    Boolean isLogin(@PathVariable("username") String username, @RequestHeader("Cookie") String cookies);
//
//    @RequestMapping(value = "/user/username/{username}", method = RequestMethod.GET)
//    Result findUserByUsername(@PathVariable("username") String username, @RequestHeader("Cookie") String cookies);
//
//    @RequestMapping(value = "/user/currentUser", method = RequestMethod.GET)
//    User getUser(@RequestHeader("Cookie") String cookies);
//
//    @RequestMapping(value = "/user/id/{uid}", method = RequestMethod.GET)
//    User findByUid(@PathVariable("uid") Long uid, @RequestHeader("Cookie") String cookies);

    @RequestMapping(value = "/user/isLogin/{username}", method = RequestMethod.GET)
    Boolean isLogin(@PathVariable("username") String username);

    @RequestMapping(value = "/user/username/{username}", method = RequestMethod.GET)
    Result findUserByUsername(@PathVariable("username") String username);

    @RequestMapping(value = "/user/currentUser", method = RequestMethod.GET)
    User getUser();

    @RequestMapping(value = "/user/id/{uid}", method = RequestMethod.GET)
    User findByUid(@PathVariable("uid") Long uid);
//
//    @RequestLine("GET /user/test/{myparam}")
//    void test(@Param("myparm") String param);

}