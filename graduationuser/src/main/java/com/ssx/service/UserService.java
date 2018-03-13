package com.ssx.service;


import com.ssx.common.service.BaseService;
import com.ssx.common.domain.Result;
import com.ssx.common.domain.User;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface UserService extends BaseService<User, Long> {
    Result findByUsername(String username, boolean returnPassword);
    Result create(String username, String password);
    Result update(String currentUsername, String newUsername, String newPassword);
    Result login(String username, String password, HttpSession session);
    Result logout(HttpSession session);
    List<User> findBySrc(String src, int pageNo, int pageSize);
}
