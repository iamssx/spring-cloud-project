package com.ssx.resource.controller;

import com.ssx.common.domain.Result;
import com.ssx.common.domain.User;
import com.ssx.resource.client.UserService;
import com.ssx.resource.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/user")
    public Result findUserOrder(int pageNo, int pageSize) {
        User user = userService.getUser();
        if (user == null) {
            return Result.ERROR;
        }
        return orderService.findByUser(user.getUid(), pageNo, pageSize);
    }
}
