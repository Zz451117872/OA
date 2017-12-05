package com.example.OA.mvc.controller.manager;

import com.example.OA.model.Privilege;
import com.example.OA.model.Role;
import com.example.OA.model.User;
import com.example.OA.model.VO.PrivilegeVO;
import com.example.OA.model.VO.RoleVO;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.manager.UserService;
import com.example.OA.util.MD5Util;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * Created by aa on 2017/10/30.
 */
@RestController
@RequestMapping("user")
public class UserController extends CommonController{

    @Autowired
    UserService userService;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse login(@RequestParam(value = "username",required = true) String username,
                                @RequestParam(value = "password",required = true) String password)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            UsernamePasswordToken token = new UsernamePasswordToken(username, MD5Util.MD5EncodeUtf8(password));
            token.setRememberMe(true);
            subject.login(token);
            return ServerResponse.createBySuccess(username);
        }
        return ServerResponse.createBySuccess(username);
    }

    @RequiresPermissions(value = "user_add")
    @RequestMapping(value = "add_or_update_user.do",method = RequestMethod.POST)
    public ServerResponse addOrUpdate(@Valid User user , BindingResult bindingResult)
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
                    return userService.updateUser(user);
                } else {
                    return userService.addUser(user);
                }
            }
            throw new AppException(Error.PARAMS_ERROR);
    }

    @RequiresPermissions(value = "user_delete")
    @RequestMapping(value = "delete_user.do",method = RequestMethod.POST)
    public ServerResponse delete(@RequestParam(value = "userId",required = true) Integer userId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return userService.deleteUser(userId);
    }

    @RequestMapping(value = "logout.do",method = RequestMethod.GET)
    public void logout()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        subject.logout();
    }

    @RequiresPermissions(value = "endow_role_to_user")
    @RequestMapping(value = "endow_role_to_user.do",method = RequestMethod.POST)
    public void endowRoleToUser(@RequestParam(value = "userId",required = true) Integer userId,
                               @RequestParam(value = "roleIds",required = true) String roleIds,
                        @RequestParam(value = "reset",required = false,defaultValue = "true") Boolean reset)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        userService.endowRoleToUser(userId,roleIds,reset);
        return;
    }

    @RequestMapping(value = "all_role_by_user.do",method = RequestMethod.POST)
    public List<RoleVO> getRoleInfoByUser(@RequestParam(value = "userId",required = true) Integer userId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return userService.getRoleInfoByUser(userId);
    }


    @RequestMapping(value = "all_privilege_by_user.do",method = RequestMethod.POST)
    public List<PrivilegeVO> getAllPrivilegeByUserId(@RequestParam(value = "userId",required = true) Integer userId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return userService.getAllPrivilegeByUserId(userId);
    }

    @RequestMapping(value = "all_user.do",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public PageInfo getAll(@RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                           @RequestParam(value = "pageSize",required = false,defaultValue = "3") Integer pageSize)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return userService.getAll(pageNum,pageSize);
    }
}
