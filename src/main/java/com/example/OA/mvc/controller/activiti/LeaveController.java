package com.example.OA.mvc.controller.activiti;

import com.example.OA.dao.LeaveMapper;
import com.example.OA.dao.UserMapper;
import com.example.OA.model.Leave;
import com.example.OA.model.User;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.activiti.LeaveWorkflowService;
import com.example.OA.util.Variable;
import org.activiti.engine.IdentityService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
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
    TaskService taskService;

    @Autowired
    LeaveWorkflowService leaveWorkflowService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    IdentityService identityService;


    //开始任务   要进行表单验证
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
                User user = getUserBySubject(subject);
                leave.setApplication(user.getId()); //设置申请人
                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("inputUser",user.getUsername());//这个变量工作流中有用，设置办理人
                return leaveWorkflowService.startWorkflow(leave, variables);
            }catch (Exception e)
            {
                throw e;
            }
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //任务列表
    @RequestMapping(value = "task_list",method = RequestMethod.POST)
    public List<Leave> taskList()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        return leaveWorkflowService.findTodoTasks(user.getUsername());
    }

    //进行中的任务
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

    //已结束任务
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

    //认领任务
    @RequestMapping(value = "claim",method = RequestMethod.POST)
    public void claim(String taskId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        if(taskId != null)
        {
            leaveWorkflowService.claim(user.getUsername(),taskId);
            return;
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //获取 请假单详细
    @RequestMapping(value = "get_leave",method = RequestMethod.POST)
    public Leave getLeave(Integer leaveId)
    {
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


    //获取 请假单详细
    @RequestMapping(value = "get_leave_vars",method = RequestMethod.POST)
    public Leave getLeaveWithVars(Integer leaveId,String taskId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
       if(leaveId != null && taskId!= null)
       {
           Leave leave = leaveMapper.selectByPrimaryKey(leaveId);
           Map<String, Object> variables = taskService.getVariables(taskId);
           leave.setVariables(variables);
           return leave;
       }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //完成任务
    @RequestMapping(value = "complete",method = RequestMethod.POST)
    public ServerResponse complete(String taskId, Variable var)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        if(taskId != null)
        {
            return leaveWorkflowService.completeTask(user.getUsername(),taskId,var);
        }
       throw new AppException(Error.PARAMS_ERROR);
    }

}
