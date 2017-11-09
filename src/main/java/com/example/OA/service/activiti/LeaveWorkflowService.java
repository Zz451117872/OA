package com.example.OA.service.activiti;

import com.example.OA.dao.LeaveMapper;
import com.example.OA.dao.UserMapper;
import com.example.OA.model.Leave;
import com.example.OA.model.activiti.TaskBean;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.CommonService;
import com.example.OA.util.Variable;
import com.google.common.collect.Lists;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by aa on 2017/11/7.
 */
@Service
public class LeaveWorkflowService extends CommonService{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LeaveMapper leaveMapper;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    HistoryService historyService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    IdentityService identityService;

    @Autowired
    UserMapper userMapper;

    public static String key = "leave";

    // 开启请假流程
    public String startWorkflow(Leave leave, Map<String, Object> variables) {
        leave.setCreateTime(new Date());
        leave.setStatus(Const.LeaveStatus.APPLICATION.getCode());
        leaveMapper.insertSelective(leave);
        logger.info("save leave: {}", leave);   //保存请假单

        String businessKey = leave.getId()+"";  //流程实例  与  请假单的一种对应关系
        ProcessInstance processInstance = null;
        try {
            // 用来设置启动流程的人员ID
            identityService.setAuthenticatedUserId(leave.getApplication()+"");

            processInstance = runtimeService.startProcessInstanceByKey(key, businessKey, variables);
            String processInstanceId = processInstance.getId();
            leave.setProcessinstanceid(processInstanceId);  //流程实例id 放入 请假单
            leaveMapper.updateByPrimaryKeySelective(leave);

            return processInstanceId;
        }catch (Exception e) {
            throw e;
        }finally
         {
            identityService.setAuthenticatedUserId(null);
        }
    }

    // 获取任务列表
    public List<Leave> findTodoTasks(String username) {
        if(username == null)
            throw new AppException(Error.PARAMS_ERROR);
       try{
           List<Leave> result = Lists.newArrayList();           //组任务 与 个人 任务一起查了
           List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(username).list();

           // 根据流程的业务ID查询实体并关联
           for (Task task : tasks) {
               String processInstanceId = task.getProcessInstanceId();
               ProcessInstance processInstance = runtimeService//
                       .createProcessInstanceQuery()//
                       .processInstanceId(processInstanceId)//
                       .active().singleResult();
               if (processInstance == null) {
                   continue;
               }
               String businessKey = processInstance.getBusinessKey();
               if (businessKey == null) {
                   continue;
               }
               Leave leave = leaveMapper.selectByPrimaryKey(Integer.parseInt(businessKey));
               leave.setTaskBean(convertTask(task));    //task 不能序列化，所以转换一下
    //           leave.setProcessInstance(processInstance);
     //          leave.setProcessDefinition(getProcessDefinition(processInstance.getProcessDefinitionId()));
               result.add(leave);
           }
           return result;
       }catch (Exception e)
       {
           throw e;
       }
    }




    public List<Leave> findRunningProcessInstaces() {

        List<Leave> result = Lists.newArrayList();
        List<ProcessInstance> processInstances = runtimeService
                .createProcessInstanceQuery()//
                .processDefinitionKey(key).active()//
                .orderByProcessDefinitionId().desc().list();

        for(ProcessInstance instance : processInstances)
        {
            String businessKey = instance.getBusinessKey();
            if (businessKey == null) {
                continue;
            }
            Leave leave = leaveMapper.selectByPrimaryKey(Integer.parseInt(businessKey));
     //       leave.setProcessInstance(instance);
     //       leave.setProcessDefinition(getProcessDefinition(instance.getProcessDefinitionId()));

            // 设置当前任务信息
            List<Task> tasks = taskService//
                    .createTaskQuery()//
                    .processInstanceId(instance.getId())//
                    .active().orderByTaskCreateTime()//
                    .desc().listPage(0, 1);
            //一般一个流程实例对应一个 任务，但并行任务时，一个流程实例对应了多个任务
            leave.setTaskBean(convertTask(tasks.get(0)));
            result.add(leave);
        }
        return result;
    }

    public List<Leave> findFinishedProcessInstaces() {

        List<Leave> result = Lists.newArrayList();
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(key).finished().orderByProcessInstanceEndTime().desc().list();

        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {

            String businessKey = historicProcessInstance.getBusinessKey();
            Leave leave = leaveMapper.selectByPrimaryKey(Integer.parseInt(businessKey));
            leave.setProcessDefinition(getProcessDefinition(historicProcessInstance.getProcessDefinitionId()));
            leave.setHistoricProcessInstance(historicProcessInstance);
            result.add(leave);
        }
        return result;
    }

    private ProcessDefinition getProcessDefinition(String processDefinitionId) {

        return repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
    }

    public ServerResponse completeTask(String username,String taskId, Variable var) {
       try{
           Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
           if(task != null)
           {
               String assignee = task.getAssignee();
               if(assignee == null || "".equals(assignee))
                   throw new AppException(Error.NO_EXISTS,"任务未分配");
               if(assignee.equals(username)) //如果任务的办理人 与 当前用户一至，就办理任务
               {
                   // 这个var，是一个普通对象，有keys,values,types 3个字段，然后你懂的
                   Map<String, Object> variables = var.getVariableMap();
                   taskService.complete(taskId, variables);
                   return ServerResponse.createBySuccess();
               }
               return ServerResponse.createByErrorMessage("指定任务办理人错误");
           }
           return ServerResponse.createByErrorMessage("没有找到指定任务");
       }catch (Exception e)
       {
           throw e;
       }
    }

    public void claim(String username, String taskId) {
        if(username == null || taskId == null)
            throw new AppException(Error.PARAMS_ERROR);
        try{
            List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
            if(tasks != null && !tasks.isEmpty())
            {
                for(Task task : tasks)
                {
                    if(task.getId().equals(taskId))
                    {
                        taskService.claim(taskId, username);
                        return;
                    }
                }
                throw new AppException(Error.NO_EXISTS,"没有该任务");
            }
            throw new AppException(Error.NO_EXISTS,"没有任务可供领取");
        }catch (Exception e)
        {
            throw e;
        }
    }
}
