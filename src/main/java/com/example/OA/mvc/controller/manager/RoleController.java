package com.example.OA.mvc.controller.manager;

import com.example.OA.model.Privilege;
import com.example.OA.model.Role;
import com.example.OA.model.VO.PrivilegeVO;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.manager.RoleService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    RoleService roleService;

    @RequiresPermissions(value = "role_add")
    @RequestMapping(value = "add_or_update_role.do",method = RequestMethod.POST)
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
                    logger.info("updateRole ing");
                    roleService.updateRole(role);
                    logger.info("updateRole end");
                    return;
                } else {
                    logger.info("addRole ing");
                    roleService.addRole(role);
                    logger.info("addRole end");
                    return;
                }
            }
            throw new AppException(Error.PARAMS_ERROR);
    }

    @RequiresPermissions(value = "role_delete")
    @RequestMapping(value = "delete_role.do",method = RequestMethod.POST)
    public void delete(@RequestParam(value = "roleId" ,required = true) Integer roleId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        roleService.deleteById(roleId);
        return;
    }

    // 授予 权限 给 角色
    @RequiresPermissions(value = "endow_privilege_to_role")
    @RequestMapping(value = "endow_privilege_to_role.do",method = RequestMethod.POST)
    public void endowPrivilegeToRole(@RequestParam(value = "roleId" ,required = true) Integer roleId,
                                    @RequestParam(value = "privilegeIds" ,required = true) String privilegeIds,
                                     @RequestParam(value = "reset" ,required = false,defaultValue = "true") Boolean reset)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        roleService.endowPrivilegeToRole(roleId,privilegeIds,reset);
        return;
    }

    //该角色 的 所有 权限信息
    @RequestMapping(value = "all_privilege_by_role.do",method = RequestMethod.POST)
    public List<PrivilegeVO> getPrivilegeInfoByRole(@RequestParam(value = "roleId" ,required = true) Integer roleId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return roleService.getPrivilegeInfoByRole(roleId);
    }


    @RequestMapping(value = "all_role.do",method = RequestMethod.POST)
    public List<Role> getAll()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return roleService.getAll();
    }
}
