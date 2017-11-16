package com.example.OA.mvc.controller;

import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by aa on 2017/11/7.
 */
@RestController
@RequestMapping("test")
public class TestShiro {

    @RequestMapping(value = "emploeme",method = RequestMethod.POST)
    public String testEmploeme()
    {
        return "xxx1";
    }

    @RequestMapping(value = "department_manager",method = RequestMethod.POST)
    @RequiresPermissions(value = {"part_add"})
    public String testDepartmentManager()
    {
        return "xxx2";
    }

    @RequestMapping(value = "genaral_manager",method = RequestMethod.POST)
    @RequiresPermissions(value = {"endow_role_to_user"})
    public String testGeneralManager()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHENTICATION);
        }
        return "xxx2";
    }

    @RequestMapping(value = "chairman",method = RequestMethod.POST)
    @RequiresPermissions(value = {"forum_manager"})
    public String testChairman()
    {
        return "xxx2";
    }

    @RequestMapping(value = "admin_user",method = RequestMethod.POST)
    @RequiresPermissions(value = {"pdf_delete"})
    public String testAdminUser()
    {
        return "xxx2";
    }
}
