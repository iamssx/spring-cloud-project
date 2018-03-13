package com.ssx.pay.controller;

import com.ssx.common.domain.Result;
import com.ssx.common.domain.User;
import com.ssx.pay.config.AlipayConfig;
import com.ssx.pay.config.WxPayConfig;
import com.ssx.pay.service.PayService;
import com.ssx.resource.client.UserService;
import com.ssx.resource.domain.Order;
import com.ssx.resource.domain.Resource;
import com.ssx.resource.service.OrderService;
import com.ssx.resource.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//@Controller
public class PayController {

    private Logger logger = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private WxPayConfig wxPayConfig;
    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    @Qualifier("alipayService")
    private PayService alipayService;
    @Autowired
    @Qualifier("weixinPayService")
    private PayService weixinPayService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/{entrance}/pay")
    public void pay(@PathVariable String entrance, Long productId, Long count, HttpServletRequest request, HttpServletResponse httpResponse, HttpSession session) throws Exception{
        if (count <= 0) {
            return ;
        }
        User user = userService.getUser();
        if (user == null) {
            return ;
        }
        Resource resource = resourceService.find(Resource.class, productId);
        if (resource == null) {
            return ;
        }

        Result result = orderService.addOrder(user.getUid(), productId, count);
        Order order = (Order) result.getEntity();
        Long oid = order.getOid();
        if ("weixin".equalsIgnoreCase(entrance)) {
            weixinPayService.pay(oid, request, httpResponse);
        }
        if ("alipay".equalsIgnoreCase(entrance)) {
            alipayService.pay(oid, request, httpResponse);
        }
    }

    @RequestMapping("/{entrance}/notify")
    public void notify(@PathVariable String entrance, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if ("weixin".equals(entrance)) {
            weixinPayService.callbackNotify(request, response);
        }

        if ("alipay".equals(entrance)) {
            alipayService.callbackNotify(request, response);
        }
    }

}
