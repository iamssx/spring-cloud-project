package com.ssx.controller;

import com.google.common.collect.Lists;
import com.ssx.client.UserService;
import com.ssx.common.domain.Result;
import com.ssx.common.domain.User;
import com.ssx.domain.App;
import com.ssx.domain.AppComment;
import com.ssx.service.AppService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/app")
public class AppController {

    @Value("${accessToken}")
    private String accessToken;

    @Autowired
    private AppService appService;
    @Autowired
    private UserService userService;

    /**
     * 根据aid查询app信息
     *
     * @param aid
     * @return
     */
    @GetMapping("/aid/{aid}")
    public App app(@PathVariable Long aid) {
        return appService.find(App.class, aid);
    }
    /**
     * add接口只有后台能用
     *
     * @return
     */
    @PostMapping("/admin/add")
    public App add(HttpServletRequest request, String accessToken, String name, String appType, String description,
                   String provider, String osVersionRequest,
                   MultipartFile pic, MultipartFile pic2, MultipartFile pic3, MultipartFile appFile) {
        if (!this.accessToken.equals(accessToken)) {
            return null;
        }
        return appService.add(request, name, appType, description, provider, osVersionRequest, Lists.newArrayList(pic, pic2, pic3), appFile);
    }
    /**
     * 接口只有后台能用
     *
     * @return
     */
    @PostMapping("/admin/delete")
    public boolean delete(String accessToken, Long aid) {
        if (!this.accessToken.equals(accessToken)) {
            return false;
        }
        return appService.delete(aid);
    }
    /**
     * 更新app
     * @return
     */
    @PostMapping("/admin/update")
    public Result update(HttpServletRequest request, Long aid, String accessToken, String name, String appType,
                         String description, String provider, String osVersionRequest,
                         MultipartFile pic, MultipartFile pic2, MultipartFile pic3, MultipartFile appFile) {
        if (!this.accessToken.equals(accessToken)) {
            return Result.ERROR;
        }
        return appService.update(request, aid, name, appType, description, provider, osVersionRequest, Lists.newArrayList(pic, pic2, pic3), appFile);
    }
    @PostMapping("/comment/add")
    public Result addComment(String content, Short score, Long aid, HttpSession session) {
        if (score > 100 || score < 0 || StringUtils.isEmpty(content)) {
            return Result.ERROR;
        }
        String id = session.getId();
        User user = userService.getUser("JSESSIONID=" + id);
        return appService.addComment(aid, content, score, user);
    }
    @GetMapping("/comment/{aid}")
    public List<AppComment> comment(@PathVariable Long aid, Integer pageNo, Integer pageSize) {
        return appService.findAppComment(aid, pageNo, pageSize);
    }
    @GetMapping("/appTye")
    public List<App> findByAppType(String appType, Integer pageNo, Integer pageSize) {
        return appService.findByAppType(appType, pageNo, pageSize);
    }
    @GetMapping("/score")
    public List<App> findByScore(Short score, Integer pageNo, Integer pageSize) {
        return appService.findByScore(score, pageNo, pageSize);
    }
    @GetMapping("/score/less")
    List<App> findByScoreLessThan(Short score, Integer pageNo, Integer pageSize) {
        return appService.findByScoreLessThan(score, pageNo, pageSize);
    }
    @GetMapping("/score/greater")
    public List<App> findByScoreGreaterThan(Short score, Integer pageNo, Integer pageSize) {
        return appService.findByScoreGreaterThan(score, pageNo, pageSize);
    }
    @GetMapping("/score/between")
    public List<App> findByScoreBetween(Short low, Short high, Integer pageNo, Integer pageSize) {
        return appService.findByScoreBetween(low, high, pageNo, pageSize);
    }
    /**
     * 根据app的uuid下载app，不用aid是因为aid时有序的，容易让人爬取
     * @param appUUID
     * @param response
     */
    @GetMapping("/download/{appUUID}")
    public void download(@PathVariable String appUUID, HttpServletResponse response) {
        appService.download(appUUID, response);
    }
    /**
     * 获取app的图片
     * @param aid
     * @return
     */
    @GetMapping("pic/{aid}")
    public List<String> pic(@PathVariable Long aid) {
        return appService.findPicByApp(aid);
    }
    /**
     * 显示图片接口
     * @param picUrl
     * @param response
     */
    @GetMapping("pic")
    public void pic(String picUrl, HttpServletResponse response) {
        appService.pic(picUrl, response);
    }

}
