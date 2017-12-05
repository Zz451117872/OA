package com.example.OA.mvc.controller.activiti;

import com.example.OA.model.User;
import com.example.OA.model.VO.BaseVO;
import com.example.OA.model.VO.CommentVO;
import com.example.OA.model.VO.HistoryTaskVO;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.common._PageInfo;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.activiti.ProcessDefinitionService;
import com.example.OA.service.activiti.WorkflowService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.activiti.engine.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by aa on 2017/11/11.
 */
@RestController
@RequestMapping("workflow")
public class WorkflowController extends CommonController{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WorkflowService workflowService;

    @Autowired
    ProcessDefinitionService processDefinitionService;

    @Autowired
    TaskService taskService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    ProcessEngineConfiguration processEngineConfiguration;

    @Autowired
    HistoryService historyService;


    //查找申请
    @RequestMapping(value = "find_applications.do",method = RequestMethod.POST)
    public _PageInfo<BaseVO> findApplication(@RequestParam(value = "status",required = true) Integer status,
                                    @RequestParam(value = "isPersonal",required = false,defaultValue = "true") Boolean isPersonal,
                                    @RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize",required = false,defaultValue = "3") Integer pageSize) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
            try{
                Const.BusinessStatus.codeof(status);
            }catch (Exception e)
            {
                e.printStackTrace();
                throw new AppException(Error.PARAMS_ERROR);
            }
        if(isPersonal)
        {
            return workflowService.findApplications(user.getId(),status,pageNum,pageSize);
        }else{
            return workflowService.findApplications(null,status,pageNum,pageSize);
        }
    }

    //查找任务
    @RequestMapping(value= "find_tasks.do",method = RequestMethod.POST)
    public _PageInfo<BaseVO> findTasks(@RequestParam(value = "isPersonal",required = false,defaultValue = "true") Boolean isPersonal,
                               @RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize",required = false,defaultValue = "3") Integer pageSize) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        if(isPersonal)
        {
            return workflowService.findTasks(user.getUsername(),pageNum,pageSize);
        }else{
            return workflowService.findTasks(null,pageNum,pageSize);
        }
    }

    //查找已完成任务
    @RequestMapping(value = "find_history_tasks.do",method = RequestMethod.POST)
    public _PageInfo<HistoryTaskVO> findHistoryTasks(@RequestParam(value = "isPersonal",required = false,defaultValue = "true") Boolean isPersonal,
                                                @RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize",required = false,defaultValue = "3") Integer pageSize) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        if(isPersonal)
        {
            return workflowService.findHisrotyTasks(user.getUsername(),pageNum,pageSize);
        }else{
            return workflowService.findHisrotyTasks(null,pageNum,pageSize);
        }
    }
    //查找评论
    @RequestMapping(value = "find_comments.do",method = RequestMethod.POST)
    public _PageInfo<CommentVO> findComments(@RequestParam(value = "pin_id",required = true) String pin_id,
                                            @RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize",required = false,defaultValue = "3") Integer pageSize)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return workflowService.findComments(pin_id,pageNum,pageSize);
    }
//=================任务操作流程==================================================
    //认领任务
    @RequestMapping(value = "claim_task.do",method = RequestMethod.POST)
    public void claim(@RequestParam(value = "taskId",required = true) String taskId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        workflowService.claim(user.getUsername(),taskId);
        return;
    }

    //委托任务
    @RequestMapping(value = "delegate_task.do",method = RequestMethod.POST)
    public void delegateTask(@RequestParam(value = "taskId",required = true) String taskId,
                             @RequestParam(value = "toUserId",required = true) Integer toUserId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        if(toUserId == user.getId())
            throw new AppException(Error.PARAMS_ERROR);
        workflowService.doDelegateTask(user.getUsername(),toUserId,taskId);
        return;
    }

    //转办任务
    @RequestMapping(value = "transfer_task.do",method = RequestMethod.POST)
    public void transferTask(@RequestParam(value = "taskId",required = true) String taskId,
                             @RequestParam(value = "toUserId",required = true) Integer toUserId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        if(toUserId == user.getId())
            throw new AppException(Error.PARAMS_ERROR);
        workflowService.doTransferTask(user.getUsername(),toUserId,taskId);
    }

    //撤回任务
    @RequestMapping(value = "revoke_task.do",method = RequestMethod.POST)
    public void revoke(@RequestParam(value = "taskId",required = true) String taskId,
                       @RequestParam(value = "processInstanceId",required = true) String processInstanceId) {

    }

    //完成任务
    @RequestMapping(value = "complete_task.do",method = RequestMethod.POST)
    public void complete(@RequestParam(value = "comment",required = false,defaultValue = "我无语") String comment,
                         @RequestParam(value = "isPass",required = true) Boolean isPass,
                         @RequestParam(value = "taskId",required = true) String taskId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        Map<String,Object> variables = Maps.newHashMap();
        variables.put("isPass",isPass);
        workflowService.completeTask(user,taskId,variables,comment);
        return;

    }

}
