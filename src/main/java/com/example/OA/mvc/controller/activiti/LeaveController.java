package com.example.OA.mvc.controller.activiti;

import com.example.OA.model.Dispose;
import com.example.OA.model.Leave;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.activiti.LeaveService;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by aa on 2017/11/3.
 */
@RestController
@RequestMapping("leave")
public class LeaveController extends CommonController{

    @Autowired
    LeaveService leaveService;

    //申请请假 ----------需要表单验证
    @RequestMapping(value = "application",method = RequestMethod.POST)
    public Map<String,Leave> application(Leave leave, String key)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(leave != null)
        {
            leave.setApplication(getUserId(subject)); //设置申请人
            return leaveService.application(leave,key);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    //取消请假
    @RequestMapping(value = "cancle_application",method = RequestMethod.POST)
    public Integer cancleApplication(String taskId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(taskId != null)
        {                               //传入 申请人 id，避免越权
            return leaveService.cancleApplication(getUserId(subject),taskId);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    //获取的我的申请,状态为 未关闭
    @RequestMapping(value = "my_application",method = RequestMethod.POST)
    public List<Leave> myApplication()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
          return leaveService.myApplication(getUserId(subject));
    }

    //获取我的历史记录
    @RequestMapping(value = "my_application_history",method = RequestMethod.POST)
    public List<Leave> myApplicationHistory()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return leaveService.myApplicationHistory(getUserId(subject));
    }

    //通过状态查询
    @RequestMapping(value = "all_leave_status",method = RequestMethod.POST)
    public List<Leave> getAllByStatus(Integer status)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(status != null)
        {
            return leaveService.getAllByStatus(status);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    //通过主键查询
    @RequestMapping(value = "get_by_id",method = RequestMethod.POST)
    public Leave getById(Integer leaveId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(leaveId != null)
        {
            return leaveService.getById(leaveId);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }


    //通过申请人查询
    @RequestMapping(value = "by_application_id_or_name",method = RequestMethod.POST)
    public List<Leave> getByApplicationIdOrName(Integer userId,String username)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(userId != null || username != null)
        {
            return leaveService.getByApplicationIdOrName(userId,username);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    //获取需要我处理的请假
    @RequestMapping(value = "need_dispose",method = RequestMethod.POST)
    public Map<String,Leave> needIDispose(String definitionKey)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
            return leaveService.needIDispose(getUserId(subject),definitionKey);
    }

}
