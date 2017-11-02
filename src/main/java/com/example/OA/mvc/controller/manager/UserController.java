package com.example.OA.mvc.controller.manager;

import com.example.OA.model.Privilege;
import com.example.OA.model.Role;
import com.example.OA.model.User;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.manager.UserService;
import com.example.OA.util.MD5Util;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Created by aa on 2017/10/30.
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "login",method = RequestMethod.POST)
    public String login(String username, String password)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            UsernamePasswordToken token = new UsernamePasswordToken(username, MD5Util.MD5EncodeUtf8(password));
            token.setRememberMe(true);
            subject.login(token);
            return username;
        }
        return username;
    }

    @RequestMapping(value = "add_user",method = RequestMethod.POST)
    public String add(User user)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(user != null)
        {
            user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
            return userService.addUser(user);
        }
       throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "update_user",method = RequestMethod.POST)
    public String update(User user)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(user != null)
        {
            user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
            return userService.updateUser(user);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "delete_user",method = RequestMethod.POST)
    public String delete(Integer userId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(userId != null)
        {
            return userService.deleteUser(userId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "logout",method = RequestMethod.GET)
    public void logout()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        subject.logout();
    }

    @RequestMapping(value = "endow_role_to_user",method = RequestMethod.POST)
    public int endowRoleToUser(Integer userId,String roleIds)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(userId != null && roleIds != null)
        {
           return userService.endowRoleToUser(userId,roleIds);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "takeback_role_from_user",method = RequestMethod.POST)
    public int takebackRoleFromUser(Integer userId,String roleIds)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(userId != null && roleIds != null)
        {
            return userService.takebackRoleFromUser(userId,roleIds);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "all_role_user",method = RequestMethod.POST)
    public List<Role> getAllRoleByUserId(Integer userId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(userId != null )
        {
            return userService.getAllRoleByUserId(userId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }


    @RequestMapping(value = "all_privilege_user",method = RequestMethod.POST)
    public List<Privilege> getAllPrivilegeByUserId(Integer userId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(userId != null )
        {
            return userService.getAllPrivilegeByUserId(userId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }
}
