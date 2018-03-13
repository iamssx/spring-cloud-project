package com.ssx.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestSwaggerController {

    @ApiOperation(value = "获取用户详细信息", notes = "this is a test hello world")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = false, dataType = "Long"),
            @ApiImplicitParam(name = "name", required = false, dataType = "String")
    })
    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }
}
