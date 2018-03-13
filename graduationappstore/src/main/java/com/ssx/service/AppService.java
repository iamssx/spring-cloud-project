package com.ssx.service;

import com.ssx.common.service.BaseService;
import com.ssx.common.domain.Result;
import com.ssx.common.domain.User;
import com.ssx.domain.App;
import com.ssx.domain.AppComment;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public interface AppService extends BaseService<App, Long> {

    App add(HttpServletRequest request, String name, String appType, String description,
            String provider, String osVersionRequest,
            List<MultipartFile> picaList, MultipartFile appFile);

    boolean delete(Long aid);

    Result update(HttpServletRequest request, Long aid, String name, String appType, String description, String provider, String osVersionRequest, ArrayList<MultipartFile> multipartFiles, MultipartFile appFile);

    Result addComment(Long aid, String content, Short score, User user);

    List<AppComment> findAppComment(@Param("aid") Long aid, Integer pageNo, Integer pageSize);

    void download(String appUUID, HttpServletResponse response);

    List<App> findByAppType(String appType, Integer pageNo, Integer pageSize);

    List<App> findByScore(Short score, Integer pageNo, Integer pageSize);

    List<App> findByScoreLessThan(Short score, Integer pageNo, Integer pageSize);

    List<App> findByScoreGreaterThan(Short score, Integer pageNo, Integer pageSize);

    List<App> findByScoreBetween(Short low, Short high, Integer pageNo, Integer pageSize);

    void pic(String picUrl, HttpServletResponse response);

    List<String> findPicByApp(Long aid);
}
