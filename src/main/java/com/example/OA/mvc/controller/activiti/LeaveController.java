package com.example.OA.mvc.controller.activiti;

import com.example.OA.dao.LeaveMapper;
import com.example.OA.model.Leave;
import com.example.OA.model.User;
import com.example.OA.model.activiti.TaskBean;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.activiti.LeaveWorkflowService;
import com.example.OA.util.Variable;
import org.activiti.engine.TaskService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aa on 2017/11/3.
 */
@RestController
@RequestMapping("leave")
public class LeaveController extends CommonController{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LeaveMapper leaveMapper;

    @Autowired
    TaskService taskService;

    @Autowired
    LeaveWorkflowService leaveWorkflowService;

    @RequestMapping(value = "start_leave_workflow",method = RequestMethod.POST)
    public ServerResponse startWorkflow(Leave leave)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        try{
            User user = getUserBySubject(subject);
            leave.setApplication(user.getId());
            Map<String, Object> variables = new HashMap<String, Object>();
            return leaveWorkflowService.startWorkflow(leave, variables);
        }catch (Exception e)
        {
            throw e;
        }
    }

    @RequestMapping(value = "task_list",method = RequestMethod.POST)
    public List<Leave> taskList()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        return leaveWorkflowService.findTodoTasks(user.getId());
    }

    @RequestMapping(value = "running_list",method = RequestMethod.POST)
    public List<Leave> runningList()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }

       return leaveWorkflowService.findRunningProcessInstaces();
    }

    @RequestMapping(value = "finished_list",method = RequestMethod.POST)
    public  List<Leave> finishedList()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }

        return leaveWorkflowService.findFinishedProcessInstaces();
    }

    @RequestMapping(value = "claim",method = RequestMethod.POST)
    public ServerResponse claim(String taskId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        taskService.claim(taskId, user.getUsername());
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "get_leave",method = RequestMethod.POST)
    public Leave getLeave(Integer leaveId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return leaveMapper.selectByPrimaryKey(leaveId);
    }


    @RequestMapping(value = "get_leave_vars",method = RequestMethod.POST)
    public Leave getLeaveWithVars(Integer leaveId,String taskId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        Leave leave = leaveMapper.selectByPrimaryKey(leaveId);
        Map<String, Object> variables = taskService.getVariables(taskId);
        leave.setVariables(variables);
        return leave;
    }

    @RequestMapping(value = "complete",method = RequestMethod.POST)
    public ServerResponse complete(String taskId, Variable var)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        try{
            Map<String, Object> variables = var.getVariableMap();
            taskService.complete(taskId, variables);
           return ServerResponse.createBySuccess();
        }catch (Exception e)
        {
            throw  e;
        }
    }

}
