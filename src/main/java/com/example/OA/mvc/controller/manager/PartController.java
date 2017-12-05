package com.example.OA.mvc.controller.manager;

import com.example.OA.model.Part;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.manager.PartService;
import com.github.pagehelper.PageInfo;
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
@RequestMapping("part")
public class PartController {

    @Autowired
    PartService partService;

    @RequiresPermissions(value = "part_add")
    @RequestMapping(value = "add_or_update_part.do",method = RequestMethod.POST)
    public void addOrUpdate(@Valid Part part , BindingResult bindingResult)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }

        if(bindingResult.hasErrors())
        {
            throw new AppException(Error.PARAMS_ERROR,bindingResult.getFieldError().getDefaultMessage());
        }
           if(part != null)
           {
               if(part.getId() != null)
               {
                   partService.updatePart(part);
                   return;
               }else{
                   partService.addPart(part);
                   return;
               }
           }
            throw new AppException(Error.PARAMS_ERROR);
    }

    @RequiresPermissions(value = "part_delete")
    @RequestMapping(value = "delete_part.do",method = RequestMethod.POST)
    public void delete(@RequestParam(value = "partId",required = true) Integer partId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        partService.deleteById(partId);
        return;
    }

    @RequestMapping(value = "all_part.do",method = RequestMethod.POST)
    public PageInfo getAll(@RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                           @RequestParam(value = "pageSize",required =  false,defaultValue = "3")Integer pageSize)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return partService.getAll(pageNum,pageSize);
    }
}
