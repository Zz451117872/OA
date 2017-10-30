package com.example.OA.mvc.controller;

import com.example.OA.model.User;
import com.example.OA.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Created by aa on 2017/10/30.
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "login",method = RequestMethod.POST)
    public String login(String username, String password, HttpSession session)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            UsernamePasswordToken token = new UsernamePasswordToken(username,password);
            token.setRememberMe(true);
            try{
                subject.login(token);
                return ((User) subject.getPrincipal()).getUsername();
            }catch(Exception e){
                return "Authenticate faild";
            }
        }
        return ((User) subject.getPrincipal()).getUsername();
    }

    @RequestMapping(value = "add",method = RequestMethod.POST)
    public String add(String username, String password)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            return "un Authenticate";
        }
        return userService.add(username,password).getUsername();
    }
}
