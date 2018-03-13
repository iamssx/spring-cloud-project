package com.ssx.service.impl;

import com.ssx.common.domain.Result;
import com.ssx.common.domain.ResultCode;
import com.ssx.common.domain.User;
import com.ssx.common.service.ABaseService;
import com.ssx.domain.UserRepository;
import com.ssx.service.UserService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl extends ABaseService<User, Long> implements UserService {

//    @Autowired
//    private UserRepository repository;

    @Override
    public Result findByUsername(String username, boolean returnPassword) {
        if (StringUtils.isEmpty(username)) {
            return Result.ERROR;
        }
        User user = ((UserRepository)repository).findByUsername(username);
        Result result = new Result();
        if (user == null) {
            result.setCode(ResultCode.USER_UN_EXISTS);
            return result;
        }
        if (!returnPassword) {
            try {
                user = (User) BeanUtils.cloneBean(user);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            user.setPassword(null);
        }
        result.setEntity(user);
        return result;
    }

    /**
     * 隔离性设置为序列化,是因为创建用户时，使用先查数据库是否存在相同的用户名，再去创建新的用户，存在虚读问题
     * 注：用户名字段设置成unique
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Result create(String username, String password) {
        User byUsername = ((UserRepository)repository).findByUsername(username);
        Result result = new Result();
        if (byUsername != null) {
            result.setCode(ResultCode.USERNAME_EXISTS);
            result.setDescription("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        User saveUser = save(user);
        result.setEntity(saveUser);
        return result;
    }

    /**
     * 隔离性设置为序列化,是因为更新用户时，使用先查数据库是否存在相同的用户名，再去创建新的用户，存在虚读问题
     * 注：用户名字段设置成unique
     *
     * @param currentUsername
     * @param newUsername
     * @param newPassword
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Result update(String currentUsername, String newUsername, String newPassword) {
        if (StringUtils.isEmpty(newPassword + newUsername)) {
            return Result.ERROR;
        }
        User currenUser = ((UserRepository)repository).findByUsername(currentUsername);
        Result result = new Result();
        if (currenUser == null) {
            result.setCode(ResultCode.USER_UN_EXISTS);
            return result;
        }

        User updateUser = ((UserRepository)repository).findByUsername(newUsername);
        if (updateUser != null) {
            result.setCode(ResultCode.USERNAME_EXISTS);
            return result;
        }
        if (StringUtils.isNotEmpty(newUsername)) {
            currenUser.setUsername(newUsername);
        }
        if (StringUtils.isNotEmpty(newPassword)) {
            currenUser.setPassword(newPassword);
        }
        try {
            updateUser = (User) BeanUtils.cloneBean(updateUser);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        updateUser.setPassword(null);
        result.setCode(ResultCode.SUCCESS);
        result.setEntity(updateUser);
        return result;
    }

    @Override
    public Result login(String username, String password, HttpSession session) {
        Result result = findByUsername(username, true);
        User entity = (User) result.getEntity();
        if (entity == null) {
            result.setCode(ResultCode.SUCCESS);
            return result;
        }
        if (StringUtils.isEmpty(password) || !password.equals(entity.getPassword())) {
            result.setCode(ResultCode.PASSWORD_ERROR);
            result.setEntity(null);
            return result;
        }
        entity.setPassword(null);
        session.setAttribute("username", entity.getUsername());
        return result;
    }

    @Override
    public Result logout(HttpSession session) {
        String sessionUsername = (String) session.getAttribute("username");
        Result result = new Result();
        if (StringUtils.isEmpty(sessionUsername)) {
            result.setCode(ResultCode.USER_UN_LOGIN);
            return result;
        }
        session.removeAttribute("username");
        return result;
    }

    @Override
    public List<User> findBySrc(String src, int pageNo, int pageSize) {
        if (StringUtils.isEmpty(src) || pageNo < 0 || pageSize <= 0) {
            return null;
        }
        PageRequest pageRequest = new PageRequest(pageNo, pageSize);
        return ((UserRepository)repository).findBySrc(src, pageRequest);
    }

}
