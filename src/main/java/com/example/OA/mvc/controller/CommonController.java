package com.example.OA.mvc.controller;

import com.example.OA.model.User;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by aa on 2017/11/1.
 */
public class CommonController {

    public  String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public Integer getUserId(Subject subject)
    {
        if(subject != null)
        {
            return ((User)subject.getPrincipal()).getId();
        }
        return null;
    }

    public User getUserBySubject(Subject subject)
    {
        Object obj = subject.getPrincipal();
        if(obj instanceof User)
        {
            return (User) obj;
        }
        return null;
    }

}
