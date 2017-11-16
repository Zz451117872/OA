package com.example.OA.mvc.controller.manager;

import com.example.OA.model.Privilege;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.manager.PrivilegeService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by aa on 2017/10/31.
 */
@RestController
@RequestMapping("privilege")
public class PrivilegeController {

    @Autowired
    PrivilegeService privilegeService;


    @RequiresPermissions(value = "privilege_add")
    @RequestMapping(value = "add_or_update_privilege",method = RequestMethod.POST)
    public void addOrUpdate(@Valid Privilege privilege , BindingResult bindingResult)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }

        if(bindingResult.hasErrors())
        {
            throw new AppException(Error.PARAMS_ERROR,bindingResult.getFieldError().getDefaultMessage());
        }

            if(privilege != null)
            {
                if(privilege.getId() != null)
                {
                    privilegeService.update(privilege);
                    return;
                }else {
                    privilegeService.add(privilege);
                    return;
                }
            }
            throw new AppException(Error.PARAMS_ERROR);
    }

    @RequiresPermissions(value = "privilege_delete")
    @RequestMapping(value = "delete_privilege",method = RequestMethod.POST)
    public void delete(@RequestParam(value = "privilegeId", required = true) Integer privilegeId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        privilegeService.deleteById(privilegeId);
        return;
    }
}
