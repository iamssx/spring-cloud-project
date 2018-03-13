package com.ssx.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.ssx.pay.config.AlipayConfig;
import com.ssx.pay.service.PayService;
import com.ssx.resource.domain.Order;
import com.ssx.resource.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Service("alipayService")
@Transactional
public class AlipayService implements PayService {

    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    private OrderService orderService;

    private AlipayClient alipayClient;

    @PostConstruct
    public void postConstruct() {
        alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                alipayConfig.getAppId(),
                alipayConfig.getAppPrivateKey(),
                alipayConfig.getFormat(),
                alipayConfig.getAppPrivateKey(),
                alipayConfig.getAplipayPublicKey(),
                alipayConfig.getSignType()); //获得初始化的AlipayClient
    }

    @Override
    public String pay(Long oid, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Order order = orderService.find(Order.class, oid);
        if (order == null) {
            return null;
        }
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        ;
        alipayRequest.setReturnUrl(alipayConfig.getReturnUrl());
        alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());//在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                "    \"out_trade_no\":\""+ oid + "\"," +
                "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                "    \"total_amount\":" + order.getAmount() + "," +
                "    \"subject\":\"" + order.getResource().getName() + "\"," +
                "    \"body\":\""+ order.getResource().getDescription() + "\"," +
                "  }");//填充业务参数
        String form = "";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=" + alipayConfig.getCharset());
        try {
            response.getWriter().write(form);//直接将完整的表单html输出到页面
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean callbackNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
        Map paramsMap = request.getParameterMap(); //将异步通知中收到的所有参数都存放到map中
        String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");// 商户订单号
        //交易状态
        String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
        boolean signVerified = AlipaySignature.rsaCheckV1(paramsMap, alipayConfig.getAplipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType()); //调用SDK验证签名
        if(signVerified){//验证成功
            if(trade_status.equals("TRADE_SUCCESS")){
                Order order = orderService.find(Order.class, Long.valueOf(out_trade_no));
                order.setState(Order.PAY);
                orderService.save(order);
            }

            PrintWriter out = response.getWriter();
            out.println("success");	//请不要修改或删除
            return true;
        } else {
            PrintWriter out = response.getWriter();
            out.println("failure");	//请不要修改或删除
        }
        return false;
    }
}
