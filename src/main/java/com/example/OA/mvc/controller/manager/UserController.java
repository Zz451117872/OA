package com.example.OA.mvc.controller.manager;

import com.example.OA.model.Privilege;
import com.example.OA.model.Role;
import com.example.OA.model.User;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.manager.UserService;
import com.example.OA.util.MD5Util;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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
    public String login(@RequestParam(value = "username",required = true) String username,
                        @RequestParam(value = "password",required = true) String password)
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

    @RequiresPermissions(value = "user_add")
    @RequestMapping(value = "add_or_update_user",method = RequestMethod.POST)
    public void addOrUpdate(@Valid User user , BindingResult bindingResult)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(bindingResult.hasErrors())
        {
            throw new AppException(Error.PARAMS_ERROR,bindingResult.getFieldError().getDefaultMessage());
        }
            if (user != null) {
                user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
                if (user.getId() != null) {
                    userService.updateUser(user);
                    return;
                } else {
                    userService.addUser(user);
                    return;
                }
            }
            throw new AppException(Error.PARAMS_ERROR);
    }

    @RequiresPermissions(value = "user_delete")
    @RequestMapping(value = "delete_user",method = RequestMethod.POST)
    public void delete(@RequestParam(value = "userId",required = true) Integer userId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        userService.deleteUser(userId);
        return;
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

    @RequiresPermissions(value = "endow_role_to_user")
    @RequestMapping(value = "endow_role_to_user",method = RequestMethod.POST)
    public void endowRoleToUser(@RequestParam(value = "userId",required = true) Integer userId,
                               @RequestParam(value = "roleIds",required = true) String roleIds)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        userService.endowRoleToUser(userId,roleIds);
        return;
    }

    @RequiresPermissions(value = "back_role_from_user")
    @RequestMapping(value = "takeback_role_from_user",method = RequestMethod.POST)
    public void takebackRoleFromUser(@RequestParam(value = "userId",required = true) Integer userId,
                                    @RequestParam(value = "roleIds",required = true) String roleIds)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        userService.takebackRoleFromUser(userId,roleIds);
        return;
    }

    @RequestMapping(value = "all_role_by_user",method = RequestMethod.POST)
    public List<Role> getAllRoleByUserId(@RequestParam(value = "userId",required = true) Integer userId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return userService.getAllRoleByUserId(userId);
    }


    @RequestMapping(value = "all_privilege_by_user",method = RequestMethod.POST)
    public List<Privilege> getAllPrivilegeByUserId(@RequestParam(value = "userId",required = true) Integer userId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return userService.getAllPrivilegeByUserId(userId);
    }
}
