package com.example.OA.mvc.controller.manager;

import com.example.OA.model.Privilege;
import com.example.OA.model.Role;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.manager.RoleService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by aa on 2017/10/31.
 */
@RestController
@RequestMapping("role")
public class RoleController {

    @Autowired
    RoleService roleService;

    @RequestMapping(value = "add_role",method = RequestMethod.POST)
    public String add(Role role)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(role != null)
        {
            return roleService.addRole(role);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "update_role",method = RequestMethod.POST)
    public String update(Role role)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(role != null)
        {
            return roleService.updateRole(role);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "delete_role",method = RequestMethod.POST)
    public String delete(Integer roleId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(roleId != null)
        {
            return roleService.deleteById(roleId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "endow_privilege_to_role",method = RequestMethod.POST)
    public int endowPrivilegeToRole(Integer roleId,String privilegeIds)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(roleId != null && privilegeIds != null)
        {
            return roleService.endowPrivilegeToRole(roleId,privilegeIds);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "takeback_privilege_from_role",method = RequestMethod.POST)
    public int takebackPrivilegeFormRole(Integer roleId,String privilegeIds)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(roleId != null && privilegeIds != null)
        {
            return roleService.takebackPrivilegeFormRole(roleId,privilegeIds);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "all_privilege_role",method = RequestMethod.POST)
    public List<Privilege> getAllPrivilegeByRoleId(Integer roleId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(roleId != null)
        {
            return roleService.getAllPrivilegeByRoleId(roleId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }
}
