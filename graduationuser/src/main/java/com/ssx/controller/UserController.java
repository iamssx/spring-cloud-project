package com.ssx.controller;

import com.ssx.common.domain.Result;
import com.ssx.common.domain.ResultCode;
import com.ssx.common.domain.User;
import com.ssx.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/uid/{uid}")
    public Result findUserById(@PathVariable long uid) {
        Result result = new Result();
        User user = userService.find(User.class, uid);
        if (user == null) {
            result.setCode(ResultCode.USER_UN_EXISTS);
            return result;
        }
        user.setPassword(null);
        result.setEntity(user);
        return result;
    }

    @GetMapping("/username/{username}")
    public Result findByUsername(@PathVariable String username) {
        return userService.findByUsername(username, false);
    }

    @GetMapping("/isLogin/{username}")
    Boolean isLogin(@PathVariable("username") String username, HttpSession session) {
        if (username == null) {
            return false;
        }
        Object user = session.getAttribute("user");
        String id = session.getId();
        System.out.println(id);
        String currUsername = (String) session.getAttribute("username");
        if (username.equals(currUsername)) {
            return true;
        }
        return false;
    }

    @GetMapping("/currentUser")
    public User getUser(HttpSession session) {
        String id = session.getId();
        String currUsername = (String) session.getAttribute("username");
        if (currUsername == null) {
            return null;
        }
        Result result = userService.findByUsername(currUsername, false);
        return (User) result.getEntity();
    }

    @PostMapping("/create")
    public Result create(String username, String password, HttpSession session) {
        if (session.getAttribute("username") != null) {
            Result result = new Result();
            result.setCode(ResultCode.USER_HAS_LOGIN);
            return result;
        }
        return userService.create(username, password);
    }

    @PostMapping("/update")
    public Result update(String newUsername, String newPassword, HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");
        return userService.update(currentUsername, newUsername, newPassword);
    }

    @PostMapping("/login")
    public Result login(String username, String password, HttpSession session) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return Result.ERROR;
        }
        return userService.login(username, password, session);
    }

    @RequestMapping("/logout")
    public Result logout(HttpSession session) {
        return userService.logout(session);
    }

    @GetMapping("/src/{src}")
    public List<User> findBySrc(@PathVariable String src, int pageNo, int pageSize) {
        return userService.findBySrc(src, pageNo, pageSize);
    }

    @Value("username")
    String username;

    @GetMapping("/test")
    public String test(HttpSession session) {
        session.setAttribute("user", "test");
        Result<User> result = userService.findByUsername("abc3", true);
        User entity = result.getEntity();
        System.out.println(result);
        System.out.println(username);
        return session.getId();
    }

    @GetMapping("/islogin")
    public String islogin(HttpSession session, HttpServletRequest request) {
        Object user = session.getAttribute("user");
        System.out.println(user);
        String id = session.getId();
        String id1 = request.getSession().getId();
        System.err.println(id);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String s = headerNames.nextElement();
            System.out.println(s + "=" + request.getHeader(s));
        }
        return id;
    }
}
