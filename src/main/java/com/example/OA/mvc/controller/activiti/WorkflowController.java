package com.example.OA.mvc.controller.activiti;

import com.example.OA.model.User;
import com.example.OA.model.activiti.BaseVO;
import com.example.OA.model.activiti.CommentVO;
import com.example.OA.model.activiti.ProcessInstanceBean;
import com.example.OA.model.activiti.TaskBean;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.activiti.ProcessDefinitionService;
import com.example.OA.service.activiti.WorkflowService;
import com.example.OA.util.ProcessDefinitionCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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

    //需要我处理的任务
    @RequestMapping(value = "todo_list",method = RequestMethod.POST)
    public List<BaseVO> todoList() {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        return workflowService.findTodoTasks(user.getUsername());
    }

    //认领任务
    @RequestMapping(value = "claim_task",method = RequestMethod.POST)
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
    @RequestMapping(value = "delegate_task",method = RequestMethod.POST)
    public void delegateTask(@RequestParam(value = "taskId",required = true) String taskId,
                             @RequestParam(value = "toUserId",required = true) Integer toUserId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        workflowService.doDelegateTask(user.getUsername(),toUserId,taskId);
        return;
    }

    //转办任务
    @RequestMapping(value = "transfer_task",method = RequestMethod.POST)
    public void transferTask(@RequestParam(value = "taskId",required = true) String taskId,
                             @RequestParam(value = "toUserId",required = true) Integer toUserId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        workflowService.doTransferTask(user.getUsername(),toUserId,taskId);
    }

    //撤回任务
    @RequestMapping(value = "revoke_task",method = RequestMethod.POST)
    public void revoke(@RequestParam(value = "taskId",required = true) String taskId,
                       @RequestParam(value = "processInstanceId",required = true) String processInstanceId) {

    }

    //完成任务
    @RequestMapping(value = "complete_task",method = RequestMethod.POST)
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

    //获取任务的所有评论
    @RequestMapping(value = "comment_by_task",method = RequestMethod.POST)
    public List<CommentVO> getCommentByTask(@RequestParam(value = "taskId",required = true) String taskId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return workflowService.getComments(taskId);
    }

    //查询已完成任务
    @RequestMapping(value = "finish_task",method = RequestMethod.POST)
    public List<Map<String,Object>> findFinishedTaskInstances() {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);

        List<BaseVO> baseVOs =  workflowService.findFinishedTaskInstances(user.getUsername());
        List<Map<String,Object>> result = Lists.newArrayList();
        try{
            if(baseVOs != null && !baseVOs.isEmpty())
            {
                for(BaseVO base : baseVOs){
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("businessType", base.getBusinesstype());
                    map.put("applicationName", base.getApplicationName());
                    map.put("taskId", base.getHistoricTaskInstance().getId());
                    map.put("processInstanceId", base.getHistoricTaskInstance().getProcessInstanceId());
                    map.put("startTime", base.getHistoricTaskInstance().getStartTime());
                    map.put("claimTime", base.getHistoricTaskInstance().getClaimTime());
                    map.put("endTime", base.getHistoricTaskInstance().getEndTime());
                    map.put("deleteReason", base.getHistoricTaskInstance().getDeleteReason());
                    map.put("version", base.getProcessDefinition().getVersion());
                    result.add(map);
                }
                return result;
            }
            return result;
        }catch (Exception e)
        {
            throw new AppException(Error.UNKNOW_EXCEPTION,e.getMessage());
        }
    }

    //正在运行的流程
    @RequestMapping(value = "running_pin",method = RequestMethod.POST)
    public List<ProcessInstanceBean> listRuningProcessInstance() {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        try {
            List<ProcessInstance> processInstances = processDefinitionService.listRuningProcess();
            List<ProcessInstanceBean> result = Lists.newArrayList();
            if(processInstances != null && !processInstances.isEmpty())
            {
                for(ProcessInstance processInstance : processInstances){
                    ProcessInstanceBean pie = new ProcessInstanceBean();
                    pie.setId(processInstance.getId());
                    pie.setProcessInstanceId(processInstance.getProcessInstanceId());
                    pie.setProcessDefinitionId(processInstance.getProcessDefinitionId());
                    pie.setActivityId(processInstance.getActivityId());
                    pie.setSuspended(processInstance.isSuspended());

                    ProcessDefinitionCache.setRepositoryService(this.repositoryService);
                    String taskName = ProcessDefinitionCache.getActivityName(processInstance.getProcessDefinitionId(), processInstance.getActivityId());
                    pie.setTaskName(taskName);
                    result.add(pie);
                }
                return result;
            }
            return result;
        } catch (Exception e) {
            throw new AppException(Error.UNKNOW_EXCEPTION,e.getMessage());
        }
    }

    //结束的流程
    @RequestMapping(value = "finish_pin",method = RequestMethod.POST)
    public List<Map<String,Object>> findFinishedProcessInstances() {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        List<BaseVO> baseVOs = workflowService.findFinishedProcessInstaces();
        List<Map<String,Object>> result = Lists.newArrayList();
        try{
            if(baseVOs != null && !baseVOs.isEmpty())
            {
                for(BaseVO base : baseVOs){
                    Map<String, Object> map=new HashMap<String, Object>();
                    map.put("businessType", base.getBusinesstype());
                    map.put("applicationName", base.getApplicationName());
                    map.put("title", base.getTitle());
                    map.put("startTime", base.getHistoricProcessInstance().getStartTime());
                    map.put("endTime", base.getHistoricProcessInstance().getEndTime());
                    map.put("deleteReason", base.getHistoricProcessInstance().getDeleteReason());
                    map.put("version", base.getProcessDefinition().getVersion());
                    result.add(map);
                }
                return result;
            }
            return result;
        }catch (Exception e){
            throw new AppException(Error.UNKNOW_EXCEPTION,e.getMessage());
        }
    }

    //通过业务类型 获取正在运行的流程
    @RequestMapping(value = "running_pin_by_type",method = RequestMethod.POST)
    public  List<Map<String,Object>> getRuningProcessInstance(@RequestParam(value = "businessType",required = true) String businessType) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        List<BaseVO> baseVOs = null;
        if(Const.BusinessType.LEAVE.equals(businessType))
        {
            baseVOs = workflowService.listRuningLeave();
        }else if(Const.BusinessType.SALARY.equals(businessType))
        {
            baseVOs = workflowService.listRuningSalaryAdjust();
        }else {
            throw new AppException(Error.PARAMS_ERROR,"error:"+businessType);
        }
        List<Map<String,Object>> result = Lists.newArrayList();
        try{
            if(baseVOs != null && !baseVOs.isEmpty())
            {
                for(BaseVO base : baseVOs){
                    Map<String, Object> map=new HashMap<String, Object>();
                    map.put("taskName", base.getTaskBean().getName());
                    map.put("taskCreateTime", base.getTaskBean().getCreateTime());
                    map.put("userName", base.getApplicationName());
                    map.put("title", base.getTitle());
                    map.put("pd_version", base.getProcessDefinition().getVersion());
                    map.put("pi_id", base.getProcessInstance().getId());
                    map.put("pi_processDefinitionId", base.getProcessInstance().getProcessDefinitionId());
                    map.put("pi_suspended", base.getProcessInstance().isSuspended());
                    map.put("businessType", base.getBusinesstype());
                    map.put("businessKey", base.getBusinesskey());
                    result.add(map);
                }
                return result;
            }
            return result;
        }catch (Exception e)
        {
            throw new AppException(Error.UNKNOW_EXCEPTION,e.getMessage());
        }
    }
}
