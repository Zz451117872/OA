package com.example.OA.mvc.controller.manager;

import com.example.OA.model.Privilege;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.manager.PrivilegeService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by aa on 2017/10/31.
 */
@RestController
@RequestMapping("privilege")
public class PrivilegeController {

    @Autowired
    PrivilegeService privilegeService;



    @RequestMapping(value = "add_privilege",method = RequestMethod.POST)
    public String add(Privilege privilege)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(privilege != null)
        {
            return privilegeService.add(privilege);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "update_privilege",method = RequestMethod.POST)
    public String update(Privilege privilege)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(privilege != null)
        {
            return privilegeService.update(privilege);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "delete_privilege",method = RequestMethod.POST)
    public String delete(Integer privilegeId)
    {

        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(privilegeId != null)
        {
            privilegeService.deleteById(privilegeId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }
}
