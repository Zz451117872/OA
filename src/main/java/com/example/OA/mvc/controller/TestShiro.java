package com.example.OA.mvc.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by aa on 2017/11/7.
 */
@RestController
@RequestMapping("test")
public class TestShiro {

    @RequestMapping(value = "test1",method = RequestMethod.POST)
    @RequiresPermissions(value = {"/add_user"})
    public String test1()
    {
        return "xxx1";
    }

    @RequestMapping(value = "test2",method = RequestMethod.POST)
    @RequiresPermissions(value = {"/add_part"})
    public String test2()
    {
        return "xxx2";
    }
}
