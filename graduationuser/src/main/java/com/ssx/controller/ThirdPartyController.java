package com.ssx.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ssx.common.domain.User;
import com.ssx.config.WexinConfig;
import com.ssx.domain.UserRepository;
import com.ssx.util.WxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import weibo4j.Oauth;
import weibo4j.Users;
import weibo4j.http.AccessToken;
import weibo4j.model.WeiboException;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

@Controller
public class ThirdPartyController {

    @Autowired
    private WexinConfig wexinConfig;

  @Autowired
    private UserRepository userRepository;

    @GetMapping("/{entrance}/login")
    public String login(@PathVariable String entrance, HttpSession session) {
        if (session.getAttribute("username") != null) {
            return "redirect:/index";

        }
        if ("weixin".equals(entrance)) {
            return "redirect:" + wexinConfig.getAuthorizeUrl();
        }
        if ("wx".equals(entrance)) {
            Oauth oauth = new Oauth();
            try {
                return "redirect:" + oauth.authorize("code");
            } catch (WeiboException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/index";
    }

    @GetMapping("/{entrance}/callback")
    public void callback(@PathVariable String entrance,  String code, HttpSession session) {

        if ("weibo".equalsIgnoreCase(entrance)) {
            Oauth oauth = new Oauth();
            AccessToken accessToken = null;
            try {
                accessToken = oauth.getAccessTokenByCode(code); //1.根据code获取accessToken
            } catch (WeiboException e) {
                e.printStackTrace();
            }
            String s = accessToken.toString();
            String weiboUid = s.substring(s.lastIndexOf("="), s.length() - 1); //2. 获取uid
            Users um = new Users(accessToken.getAccessToken());
            try {
                weibo4j.model.User user = um.showUserById(weiboUid); //3. 根据accessToken和uid获取用户信息
                User weiboUser = userRepository.findBySrcAndSrcId("weibo", weiboUid);
                if (weiboUser == null) {
                    weiboUser = new User();
                    weiboUser.setUsername(UUID.randomUUID().toString());
                    weiboUser.setSrc("weibo");
                    weiboUser.setSrcId(weiboUid);
                    userRepository.save(weiboUser);
                }
                session.setAttribute("username", weiboUser);
                session.setAttribute("weiboAccessToken", accessToken.getAccessToken());
            } catch (WeiboException e) {
                e.printStackTrace();
            }
        }

        if ("wx".equalsIgnoreCase(entrance)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(wexinConfig.getAccessTokenUrl());
            stringBuilder.append("?appid=");
            stringBuilder.append(wexinConfig.getAppID());
            stringBuilder.append("&secret=");
            stringBuilder.append(wexinConfig.getAppSecret());
            stringBuilder.append("&code=");
            stringBuilder.append(code);
            stringBuilder.append("&grant_type=authorization_code");
            String accessTokenUrl = stringBuilder.toString();

            JSONObject accessTokenJsonObject = WxUtil.doGetJson(accessTokenUrl);
            //网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
            String accessToken = accessTokenJsonObject.getString("access_token");
            //access_token接口调用凭证超时时间，单位（秒）
            String expiresIn = accessTokenJsonObject.getString("expires_in");
            //用户刷新access_token
            String refreshToken = accessTokenJsonObject.getString("refresh_token");
            //用户唯一标识
            String openid = accessTokenJsonObject.getString("openid");
            //用户授权的作用域，使用逗号（,）分隔
            String scope = accessTokenJsonObject.getString("scope");
            JSONObject userInfoJsonObject = WxUtil.doGetJson("" +
                    "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=" + accessToken +
                    "&openid=" + openid +
                    "&lang=" + wexinConfig.getLang());
            //用户昵称
            String nickname = userInfoJsonObject.getString("nickname");
            //用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
            String sex = userInfoJsonObject.getString("sex");
            //用户个人资料填写的省份
            String province = userInfoJsonObject.getString("province");
            //普通用户个人资料填写的城市
            String city = userInfoJsonObject.getString("city");
            //国家，如中国为CN
            String country = userInfoJsonObject.getString("country");
            //用户头像，最后一个数值代表正方形头像大小
            // （有0、46、64、96、132数值可选，0代表640*640正方形头像）
            // 用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
            String headimgurl = userInfoJsonObject.getString("headimgurl");
            //用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
            JSONArray privilege = userInfoJsonObject.getJSONArray("privilege");
            //只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
            String unionid = userInfoJsonObject.getString("unionid");
            //用户的唯一标识
            String openid1 = userInfoJsonObject.getString("openid");

            User weixinUser = userRepository.findBySrcAndSrcId("weixin", openid);
            if (weixinUser == null) {
                weixinUser = new User();
                weixinUser.setUsername(UUID.randomUUID().toString());
                weixinUser.setSrc("weixin");
                weixinUser.setSrcId(openid);
                userRepository.save(weixinUser);
            }
            session.setAttribute("username", weixinUser.getUsername());
        }
    }


    @GetMapping("/{entrance}/share")
    @ResponseBody
    public String share(@PathVariable String entrance, String url, HttpSession session)throws Exception {
        if ("weixin".equalsIgnoreCase(entrance)) {
                int widthHeight = 300;
                String ecLevel = "L";
                int margin = 0;
                url = urlEncode(url);
                String qrFromGoogle = "http://pan.baidu.com/share/qrcode?" +
                        "w=" + widthHeight +
                        "&h=" + widthHeight +
                        "&url=" + url;
                return qrFromGoogle;
        }
        return null;
    }


    private String urlEncode(String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src, "UTF-8").replace("+", "%20");
    }

}
