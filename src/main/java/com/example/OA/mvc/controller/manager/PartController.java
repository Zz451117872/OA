package com.example.OA.mvc.controller.manager;

import com.example.OA.model.Part;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.manager.PartService;
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
@RequestMapping("part")
public class PartController {

    @Autowired
    PartService partService;

    @RequestMapping(value = "add_part",method = RequestMethod.POST)
    public String add(Part part)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(part != null)
        {
            return partService.addPart(part);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "update_part",method = RequestMethod.POST)
    public String update(Part part)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(part != null)
        {
            return partService.updatePart(part);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "delete_part",method = RequestMethod.POST)
    public String delete(Integer partId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(partId != null)
        {
            return partService.deleteById(partId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

}
