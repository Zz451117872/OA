package com.example.OA.mvc.controller.manager;

import com.example.OA.model.Privilege;
import com.example.OA.model.VO.PrivilegeVO;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.manager.PrivilegeService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
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
@RequestMapping("privilege")
public class PrivilegeController {

    @Autowired
    PrivilegeService privilegeService;


    @RequiresPermissions(value = "privilege_add")
    @RequestMapping(value = "add_or_update_privilege.do",method = RequestMethod.POST)
    public ServerResponse addOrUpdate(@Valid Privilege privilege , BindingResult bindingResult)
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
                  return  privilegeService.update(privilege);
                }else {
                   return privilegeService.add(privilege);
                }
            }
            throw new AppException(Error.PARAMS_ERROR);
    }

    @RequiresPermissions(value = "privilege_delete")
    @RequestMapping(value = "delete_privilege.do",method = RequestMethod.POST)
    public void delete(@RequestParam(value = "privilegeId", required = true) Integer privilegeId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        privilegeService.deleteById(privilegeId);
        return ;
    }

    //获取顶级权限
    @RequestMapping(value = "get_top_privilege.do",method = RequestMethod.POST)
    public List<Privilege> getTopPrivilege()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return privilegeService.getTopPrivilege();
    }


    //获取子权限
    @RequestMapping(value = "get_child.do",method = RequestMethod.POST)
    public List<PrivilegeVO> getChild(@RequestParam(value = "parentId", required = true) Integer parentId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return privilegeService.getChild(parentId);
    }
}
