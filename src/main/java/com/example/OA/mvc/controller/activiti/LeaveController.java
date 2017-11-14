package com.example.OA.mvc.controller.activiti;

import com.example.OA.dao.activiti.LeaveMapper;
import com.example.OA.model.activiti.Leave;
import com.example.OA.model.User;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.activiti.WorkflowService;
import com.example.OA.util.BeanUtils;
import com.google.common.collect.Maps;
import org.activiti.engine.RuntimeService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

/**
 * Created by aa on 2017/11/3.
 */
@RestController
@RequestMapping("leave")
public class LeaveController extends CommonController{

    @Autowired
    LeaveMapper leaveMapper;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    RuntimeService runtimeService;

    //启动请假流程   要进行表单验证
    @RequestMapping(value = "start_leave_workflow",method = RequestMethod.POST)
    public String startWorkflow(Leave leave) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(leave != null)
        {
            try{
                //BaseVO 相关属性
                User user = getUserBySubject(subject);
                leave.setApplication(user.getId()); //设置申请人
                leave.setApplicationName(user.getUsername());
                leave.setBusinesstype(Const.BusinessType.LEAVE);//业务类型

                //leave 相关相关属性
                leave.setCreateTime(new Date());
                leave.setStatus(Const.WorkflowStatus.APPLICATION.getCode());

                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("inputUser",user.getUsername());//设置申请人变量
                return workflowService.startLeaveWorkflow(leave, variables);
            }catch (Exception e)
            {
                throw e;
            }
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //按状态获取请假单信息,若不传 则表示查询所有
    @RequestMapping(value = "leave_by_status",method = RequestMethod.POST)
    public List<Leave> getLeavesByStatus(Integer status) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        try{
            Const.WorkflowStatus.codeof(status);   // 检查传入状态参数
        }catch (Exception e)
        {
            throw new AppException(Error.PARAMS_ERROR,"status 参数错误");
        }
        User user = getUserBySubject(subject);
       return leaveMapper.getByApplicationAndStatus(user.getId(),status);
    }

    //获取 请假单详细
    @RequestMapping(value = "leave_by_id",method = RequestMethod.POST)
    public Leave getLeave(Integer leaveId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(leaveId != null)
        {
            return leaveMapper.selectByPrimaryKey(leaveId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }



    //请假申请修改，重新申请
    @RequestMapping(value = "modify_leave",method = RequestMethod.POST)
    public void modifyLeave(Leave leave,Boolean reApply,String taskId,String comment) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        try{
            Map<String,Object> variables = Maps.newHashMap();
            variables.put("reApply",reApply);
            if(reApply)
            {   //可以修改的内容：请假天数，原因，开始结束时间，请假类型
                variables.put("leaveNumber",leave.getLeaveNumber());
                variables.put("reason",leave.getReason());
                variables.put("startTime",leave.getStartTime());
                variables.put("endTime",leave.getEndTime());
                variables.put("leaveType",leave.getLeaveType());
            }
            workflowService.completeTask(user,taskId,variables,comment);
            return;
        }catch (Exception e)
        {
            throw e;
        }
    }


}
