package com.ssx.pay.service.impl;

import com.ssx.pay.config.WxPayConfig;
import com.ssx.pay.service.PayService;
import com.ssx.pay.util.HttpUtil;
import com.ssx.pay.util.PayCommonUtil;
import com.ssx.pay.util.XmlUtil;
import com.ssx.resource.domain.Order;
import com.ssx.resource.service.OrderService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Service("weixinPayService")
@Transactional
public class WeixinPayService implements PayService {

    @Autowired
    private WxPayConfig wxPayConfig;
    @Autowired
    private OrderService orderService;

    @Override
    public String pay(Long oid, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Order order = orderService.find(Order.class, oid);
        if (order == null) {
            return null ;
        }
        String appId = wxPayConfig.getAppId();
        String mchId = wxPayConfig.getMchId();
        String apiKey = wxPayConfig.getApiKey();

        String currentTime = PayCommonUtil.getCurrentTime();
        String strTime = currentTime.substring(8);
        String strRandom = PayCommonUtil.buildRandom(4) + "";
        String nonceStr = strTime + strRandom;
        TreeMap<String, Object> paramMap = new TreeMap<>();

        paramMap.put("appid", appId);
        paramMap.put("mch_id", mchId);
        paramMap.put("nonce_str", nonceStr);
        paramMap.put("body", order.getResource().getName());
        paramMap.put("out_trade_no", oid);
        paramMap.put("total_fee", order.getAmount());
        paramMap.put("spbill_create_ip", getRequestRealIp(request));
        paramMap.put("notify_url", wxPayConfig.getNotifyUrl());
        paramMap.put("trade_type", wxPayConfig.getTradeType());

        String sign = PayCommonUtil.createSign("UTF-8", paramMap,apiKey);
        paramMap.put("sign", sign);

        String requestXml = PayCommonUtil.getRequestXml(paramMap);
        String resXml = HttpUtil.postData(wxPayConfig.getUfdoderUrl(), requestXml);
        Map<String, String> map = XmlUtil.doXmlParse(resXml);
        String codeUrl = map.get("code_url");
        return qrFromGoole(codeUrl); //返回二维码图片地址
    }

    @Override
    public boolean callbackNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
        //读取参数
        InputStream inputStream;
        inputStream = request.getInputStream();
        String s = IOUtils.toString(inputStream, "UTF-8");
        inputStream.close();

        //解析xml成map
        Map<String, String> resMap;
        resMap = XmlUtil.doXmlParse(s);

        //过滤空 设置 TreeMap
        SortedMap<String, String> packageParams = new TreeMap<>();
        Iterator<String> it = resMap.keySet().iterator();
        while (it.hasNext()) {
            String parameter = it.next();
            String parameterValue = resMap.get(parameter);

            String v = "";
            if (null != parameterValue) {
                v = parameterValue.trim();
            }
            packageParams.put(parameter, v);
        }

        // 账号信息
        String key = wxPayConfig.getApiKey(); // key

        //判断签名是否正确
        if (PayCommonUtil.isTenpaySign("UTF-8", packageParams, key)) {
            String resXml = "";
            if ("SUCCESS".equals(packageParams.get("result_code"))) {
                // 这里是支付成功
                String mch_id = packageParams.get("mch_id");
                String openid = packageParams.get("openid");
                String is_subscribe = packageParams.get("is_subscribe");
                String out_trade_no = packageParams.get("out_trade_no");
                String total_fee = packageParams.get("total_fee");

                //////////执行自己的业务逻辑////////////////
                Order order = orderService.find(Order.class, Long.valueOf(out_trade_no));
                order.setState(Order.PAY);
                orderService.save(order);
                //通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

            } else {
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            }
            //------------------------------
            //处理业务完毕
            //------------------------------
            OutputStream outputStream = response.getOutputStream();
            IOUtils.write(resXml, outputStream);
            outputStream.flush();
            outputStream.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将url生成二维码图片url
     * @param url
     * @return
     * @throws Exception
     */
    private String qrFromGoole(String url) throws Exception {
        int widthHeight = 300;
        String ecLevel = "L";
        int margin = 0;
        url = urlEncode(url);
        String qrFromGoogle = "http://chart.apis.google.com/chart?chs=" + widthHeight +
                "x" + widthHeight +
                "&cht=qr&chld=" + ecLevel + "|" + margin + "&chl=" + url;
        return qrFromGoogle;
    }

    private String urlEncode(String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src, "UTF-8").replace("+", "%20");
    }

    public static String getRequestRealIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }

        if (!checkIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!checkIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!checkIp(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (!checkIp(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private static boolean checkIp(String ip) {
        if (ip == null || ip.length() == 0 || "unkown".equalsIgnoreCase(ip) ) {
            return false;
        }
        return true;
    }
}
