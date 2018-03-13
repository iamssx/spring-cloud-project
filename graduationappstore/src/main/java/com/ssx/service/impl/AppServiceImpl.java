package com.ssx.service.impl;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.ssx.common.service.ABaseService;
import com.ssx.common.domain.Result;
import com.ssx.common.domain.User;
import com.ssx.domain.*;
import com.ssx.service.AppService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AppServiceImpl extends ABaseService<App, Long> implements AppService {

    private AppRepository appRepository;
    @Autowired
    private AppCommentRepository appCommentRepository;
    @Autowired
    private AppPicUrlRepository appPicUrlRepository;

    @Value("${appRoot}")
    private String appRoot;

    //todo 有错误返回错误代码

    @Autowired
    protected AppServiceImpl(AppRepository repository, RedisTemplate redisTemplate) {
        super(repository, redisTemplate);
        this.appRepository = repository;
    }

    @Override
    public boolean delete(Long aid) {
        App app = find(App.class, aid);
        String appFileUri = app.getFileUri();
        File file = new File(appFileUri);
        if (file.exists()) {
            file.delete();
        }
        List<AppPicUrl> byApp = appPicUrlRepository.findByApp(app);
        for (AppPicUrl picUrl : byApp) {
            String url = picUrl.getUrl();
            File file1 = new File(url);
            if (file1.exists()) {
                file1.delete();
            }
        }
        appPicUrlRepository.deleteByApp(app);
        appCommentRepository.deleteByApp(app);
        if (app != null) {
            delete(app);
            return true;
        }
        return false;
    }

    @Override
    public Result update(HttpServletRequest request, Long aid, String name, String appType, String description, String provider,
                         String osVersionRequest, ArrayList<MultipartFile> picList, MultipartFile appFile) {
        App app = find(App.class, aid);
        if (app == null) {
            return Result.ERROR;
        }
        if (name != null) {
            if (appRepository.findByName(name) != null) {
                return null;
            }
            app.setName(name);
            if (picList == null || picList.size() != 3) {
                List<AppPicUrl> byApp = appPicUrlRepository.findByApp(app);
                int i = 0;
                for (AppPicUrl picUrl : byApp) {
                    i++;
                    String url = picUrl.getUrl();
                    String postFix = url.substring(url.lastIndexOf("."));
                    try {
                        Files.copy(new File(url), new File(appRoot + "/pic", name + i + postFix));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (appType != null) {
            app.setAppType(appType);
        }
        if (description != null) {
            app.setDescription(description);
        }
        if (provider != null) {
            app.setProvider(provider);
        }
        if (osVersionRequest != null) {
            app.setOsVersionRequest(osVersionRequest);
        }

        if (appFile != null) {
            String downloadUrl = app.getFileUri();
            File oldFile = new File(downloadUrl);
            if (oldFile.exists()) {
                oldFile.delete();
            }
            String targetFileName = appFile.getOriginalFilename();
            targetFileName = name + ". " + targetFileName.substring(targetFileName.lastIndexOf(".") + 1);
            File targetFile = new File(appRoot + "/app", targetFileName);
            try {
                Files.createParentDirs(targetFile);
                appFile.transferTo(targetFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            app.setFileUri(targetFile.getPath());
        }

        app = save(app);
        if (picList != null && picList.size() == 3) {
            List<AppPicUrl> oldPicList = appPicUrlRepository.findByApp(app);
            for (AppPicUrl url : oldPicList) {
                File file = new File(url.getUrl());
                if (file.exists()) {
                    file.delete();
                }
            }
            int i = 0;
            for (MultipartFile file : picList) {
                i++;
                String targetFileName = file.getOriginalFilename();
                targetFileName = name + i + targetFileName.substring(targetFileName.lastIndexOf(".") + 1);
                File targetFile = new File(appRoot + "/pic", targetFileName);
                try {
                    Files.createParentDirs(targetFile);
                    file.transferTo(targetFile);
                    AppPicUrl appPicUrl = new AppPicUrl();
                    appPicUrl.setUrl(targetFile.getAbsolutePath());
                    appPicUrl.setApp(app);
                    appPicUrlRepository.save(appPicUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Result result = new Result();
        result.setEntity(app);
        return result;
    }

    @Override
    public Result addComment(Long aid, String content, Short score, User user) {
        if (aid <= 0 || content == null || content.length()>20 || score <= 0 || score > 100) {
            return Result.ERROR;
        }
        App app = find(App.class, aid);
        if (app == null) {
            return Result.ERROR;
        }
        AppComment appComment = new AppComment();
        appComment.setApp(app);
        appComment.setContent(content);
        appComment.setUser(user);
        AppComment comment = appCommentRepository.save(appComment);
        appRepository.incrTotalPeople(1L, aid);
        appRepository.incrScore(score, aid);
        Result result = new Result();
        result.setEntity(comment);
        return result;
    }

    @Override
    public List<AppComment> findAppComment(Long aid, Integer pageNo, Integer pageSize) {
        if (pageNo < 0 || pageSize < 0 || pageSize > 100) {
            return null;
        }
        App app = find(App.class, aid);
        if (app == null) {
            return null;
        }
        Pageable pageable = new PageRequest(pageNo, pageSize);
        List<AppComment> appCommentList = appCommentRepository.findByApp(app, pageable);
        return appCommentList;
    }

    @Override
    public void download(String appUUID, HttpServletResponse response) {
        App app = appRepository.findByUuid(appUUID);
        try {
            String appFileUri = app.getFileUri();
            byte[] data = IOUtils.toByteArray(new FileInputStream(appFileUri));
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + app.getName() + appFileUri.substring(appFileUri.lastIndexOf(".")) + "\"");
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("application/octet-stream;charset=UTF-8");

            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pic(String picUrl, HttpServletResponse response) {
        try {
            byte[] data = IOUtils.toByteArray(new FileInputStream(appRoot + "/pic/" + picUrl));
            response.reset();
            response.setContentType("text/html; charset=UTF-8");
            response.setContentType("image/jpeg");

            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> findPicByApp(Long aid) {
        App app = find(App.class, aid);
        if (app == null) {
            return null;
        }
        List<AppPicUrl> byApp = appPicUrlRepository.findByApp(app);
        ArrayList<String> list = Lists.newArrayList();
        for (AppPicUrl appPicUrl : byApp) {
            String url = appPicUrl.getUrl();
            list.add(url.substring(url.lastIndexOf("\\")));
        }
        return list;
    }

    @Override
    public List<App> findByAppType(String appType, Integer pageNo, Integer pageSize) {
        if (StringUtils.isEmpty(appType) || pageNo < 0 || pageSize < 0) {
            return null;
        }
        PageRequest pageRequest = new PageRequest(pageNo, pageSize);
        return appRepository.findByAppType(appType, pageRequest);
    }

    @Override
    public List<App> findByScore(Short score, Integer pageNo, Integer pageSize) {
        if (score < 0 || score > 100 || pageNo < 0 || pageSize < 0) {
            return null;
        }
        PageRequest pageRequest = new PageRequest(pageNo, pageSize);
        return appRepository.findByScore(score, pageRequest);
    }

    @Override
    public List<App> findByScoreLessThan(Short score, Integer pageNo, Integer pageSize) {
        if (score < 0 || score > 100 || pageNo < 0 || pageSize < 0) {
            return null;
        }
        PageRequest pageRequest = new PageRequest(pageNo, pageSize);
        List<App> list;
        list = appRepository.findByScoreLessThan(score, pageRequest);
        return list;
    }

    @Override
    public List<App> findByScoreGreaterThan(Short score, Integer pageNo, Integer pageSize) {
        if (score < 0 || score > 100 || pageNo < 0 || pageSize < 0) {
            return null;
        }
        PageRequest pageRequest = new PageRequest(pageNo, pageSize);
        return appRepository.findByScoreGreaterThan(score, pageRequest);
    }

    @Override
    public List<App> findByScoreBetween(Short low, Short high, Integer pageNo, Integer pageSize) {
        if (low < 0 || low > 100 || high < 0 || high > 100 || pageNo < 0 || pageSize < 0) {
            return null;
        }
        PageRequest pageRequest = new PageRequest(pageNo, pageSize);
        return appRepository.findByScoreBetween(low, high, pageRequest);
    }

    @Override
    @Transactional
    public App add(HttpServletRequest request, String name, String appType, String description, String provider, String osVersionRequest, List<MultipartFile> picList, MultipartFile appFile) {

        App app = new App();
        if (name == null) {
            return null;
        } else {
            App existApp = appRepository.findByName(name);
            if (existApp != null) {
                return null;
            }
            app.setName(name);
        }
        if (appType == null) {
            return null;
        } else {
            app.setAppType(appType);
        }
        if (description == null) {
            return null;
        } else {
            app.setDescription(description);
        }
        if (provider == null) {
            return null;
        } else {
            app.setProvider(provider);
        }
        if (osVersionRequest == null) {
            return null;
        } else {
            app.setOsVersionRequest(osVersionRequest);
        }

        if (appFile == null) {
            return null;
        } else {
            String targetFileName = appFile.getOriginalFilename();
            targetFileName = name + ". " + targetFileName.substring(targetFileName.lastIndexOf(".") + 1);
            File targetFile = new File(appRoot + "/app", targetFileName);
            try {
                Files.createParentDirs(targetFile);
                appFile.transferTo(targetFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            app.setFileUri(targetFile.getPath());
        }

        app.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
        app.setTotalPeople(0L);
        app.setUpdateDate(new Date());
        app.setScore((short) 0);
        app = save(app);
        if (picList == null || picList.size() != 3) {
            return null;
        } else {
            int i = 0;
            for (MultipartFile file : picList) {
                i++;
                String targetFileName = file.getOriginalFilename();
                targetFileName = name + i + targetFileName.substring(targetFileName.lastIndexOf("."));
                File targetFile = new File(appRoot + "/pic", targetFileName);
                try {
                    Files.createParentDirs(targetFile);
                    file.transferTo(targetFile);
                    AppPicUrl appPicUrl = new AppPicUrl();
                    appPicUrl.setUrl(targetFile.getAbsolutePath());
                    appPicUrl.setApp(app);
                    appPicUrlRepository.save(appPicUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return app;
    }
}
