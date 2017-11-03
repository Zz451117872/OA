package com.example.OA.mvc.controller.activiti;

import com.example.OA.model.Dispose;
import com.example.OA.model.User;
import com.example.OA.mvc.common.VO.DisposeVO;
import com.example.OA.mvc.common.VO.LeaveVO;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.activiti.DisposeService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by aa on 2017/11/3.
 */
@RestController
@RequestMapping("dispose")
public class DisposeController extends CommonController{

    @Autowired
    DisposeService disposeService;

    //处理申请
    @RequestMapping(value = "audit_application",method = RequestMethod.POST)
    public LeaveVO auditApplication(String taskId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(taskId != null )
        {
            return disposeService.auditApplication(getUserId(subject),taskId);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    @RequestMapping(value = "get_dispose_auditer",method = RequestMethod.POST)
    public List<LeaveVO> getByAuditer(Integer userId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(userId != null )
        {
            return disposeService.getByAuditer(userId);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    @RequestMapping(value = "get_auditer_leave",method = RequestMethod.POST)
    public List<User> getAuditerByLeave(Integer leaveId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(leaveId != null )
        {
            return disposeService.getAuditerByLeave(leaveId);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    @RequestMapping(value = "all",method = RequestMethod.POST)
    public List<Dispose> getAll()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return disposeService.getAll();
    }
}
