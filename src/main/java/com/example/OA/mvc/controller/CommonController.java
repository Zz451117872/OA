package com.example.OA.mvc.controller;

import com.example.OA.model.User;
import org.apache.shiro.subject.Subject;

/**
 * Created by aa on 2017/11/1.
 */
public class CommonController {

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
