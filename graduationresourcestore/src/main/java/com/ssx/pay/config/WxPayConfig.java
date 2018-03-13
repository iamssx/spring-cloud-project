package com.ssx.pay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource("config/weixinPay.properties")
@ConfigurationProperties
@Component
public class WxPayConfig {

    private String appId;
    private String mchId;       //商业号
    private String apiKey;
    private String notifyUrl;   //回调url
    private String tradeType;   //交易类型
    private String UfdoderUrl;



    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getUfdoderUrl() {
        return UfdoderUrl;
    }

    public void setUfdoderUrl(String ufdoderUrl) {
        UfdoderUrl = ufdoderUrl;
    }
}
