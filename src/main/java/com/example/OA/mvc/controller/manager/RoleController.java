package com.example.OA.mvc.controller.manager;

import com.example.OA.model.Privilege;
import com.example.OA.model.Role;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.manager.RoleService;
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
import java.util.List;

/**
 * Created by aa on 2017/10/31.
 */
@RestController
@RequestMapping("role")
public class RoleController {

    @Autowired
    RoleService roleService;

    @RequiresPermissions(value = "role_add")
    @RequestMapping(value = "add_or_update_role",method = RequestMethod.POST)
    public void addOrUpdate(@Valid Role role , BindingResult bindingResult)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(bindingResult.hasErrors())
        {
            throw new AppException(Error.PARAMS_ERROR,bindingResult.getFieldError().getDefaultMessage());
        }
            if (role != null) {
                if (role.getId() != null) {
                    roleService.updateRole(role);
                    return;
                } else {
                    roleService.addRole(role);
                    return;
                }
            }
            throw new AppException(Error.PARAMS_ERROR);
    }

    @RequiresPermissions(value = "role_delete")
    @RequestMapping(value = "delete_role",method = RequestMethod.POST)
    public void delete(@RequestParam(value = "roleId" ,required = true) Integer roleId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        roleService.deleteById(roleId);
        return;
    }

    @RequiresPermissions(value = "endow_privilege_to_role")
    @RequestMapping(value = "endow_privilege_to_role",method = RequestMethod.POST)
    public void endowPrivilegeToRole(@RequestParam(value = "roleId" ,required = true) Integer roleId,
                                    @RequestParam(value = "privilegeIds" ,required = true) String privilegeIds)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        roleService.endowPrivilegeToRole(roleId,privilegeIds);
        return;
    }

    @RequiresPermissions(value = "back_privilege_from_role")
    @RequestMapping(value = "takeback_privilege_from_role",method = RequestMethod.POST)
    public void takebackPrivilegeFromRole(@RequestParam(value = "roleId" ,required = true) Integer roleId,
                                          @RequestParam(value = "privilegeIds" ,required = true) String privilegeIds)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        roleService.takebackPrivilegeFormRole(roleId,privilegeIds);
        return;
    }

    @RequestMapping(value = "all_privilege_by_role",method = RequestMethod.POST)
    public List<Privilege> getAllPrivilegeByRoleId(@RequestParam(value = "roleId" ,required = true) Integer roleId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return roleService.getAllPrivilegeByRoleId(roleId);
    }
}
