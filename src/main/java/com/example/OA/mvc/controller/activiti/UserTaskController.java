package com.example.OA.mvc.controller.activiti;

import com.example.OA.dao.activiti.UserTaskMapper;
import com.example.OA.model.activiti.UserTask;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by aa on 2017/11/11.
 */
@RestController
@RequestMapping("user_task")
public class UserTaskController {

    @Autowired
    UserTaskMapper userTaskMapper;

    @Autowired
    RepositoryService repositoryService;

    //获取所有 用户任务 信息
    @RequestMapping(value = "list_user_task",method = RequestMethod.POST)
    public List<UserTask> listUserTask(@RequestParam(value = "procDefKey",required = true) String procDefKey){
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return userTaskMapper.getByPdfKey(procDefKey);
    }

    //初始化所有 流程定义 的用户任务
    @RequestMapping(value = "init_all_pdf",method = RequestMethod.POST)
    @Transactional
    public void initializationAllPdf()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        try{
            userTaskMapper.deleteAll();
            List<ProcessDefinition> processDefinitionList = repositoryService//
                    .createProcessDefinitionQuery()//
                    .orderByDeploymentId().desc().list();

            if(processDefinitionList != null && !processDefinitionList.isEmpty())
            {
                for(ProcessDefinition processDefinition : processDefinitionList)
                {
                    loadProcessDefinition(processDefinition);
                }
                return;
            }
            throw new AppException(Error.TARGET_NO_EXISTS,"没有流程定义");
        }catch (Exception e)
        {
            throw e;
        }
    }

    //初始化单个 流程定义 用户任务
    @RequestMapping(value = "init_single_pdf",method = RequestMethod.POST)
    public void initializationSinglePdf(@RequestParam(value = "processDefinitionId",required = true) String processDefinitionId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        try{
            ProcessDefinition processDefinition = repositoryService//
                    .createProcessDefinitionQuery()//
                    .processDefinitionId(processDefinitionId).singleResult();
            if(processDefinition != null)
            {
                loadProcessDefinition(processDefinition);
                return;
            }
            throw new AppException(Error.TARGET_NO_EXISTS,"流程定义不存在");
        }catch (Exception e)
        {
            throw e;
        }
    }

    private void loadProcessDefinition(ProcessDefinition processDefinition) {
        try {
            ProcessDefinitionEntity processDef = (ProcessDefinitionEntity) repositoryService//
                    .getProcessDefinition(processDefinition.getId());
            List<ActivityImpl> activitiList = processDef.getActivities();

            for (ActivityImpl activity : activitiList) {
                ActivityBehavior activityBehavior = activity.getActivityBehavior();
                //是否为用户任务
                if (activityBehavior instanceof UserTaskActivityBehavior) {
                    UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
                    TaskDefinition taskDefinition = userTaskActivityBehavior.getTaskDefinition();
                    //任务所属角色
                    String taskDefKey = taskDefinition.getKey();
                    Expression taskName = taskDefinition.getNameExpression();

                    UserTask userTask = new UserTask();
                    userTask.setProcdefkey(processDefinition.getKey());
                    userTask.setProcdefname(processDefinition.getName());
                    userTask.setTaskdefkey(taskDefKey);
                    userTask.setTaskname(taskName.toString());
                    userTaskMapper.insertSelective(userTask);
                }
            }
        }catch (Exception e)
        {
            throw new AppException(Error.UNKNOW_EXCEPTION,e.getMessage());
        }
    }

    //设置办理人信息
    @RequestMapping(value = "set_approver",method = RequestMethod.POST)
    public void setApprover(String procDefKey, HttpServletRequest request)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        try{
            if(procDefKey != null) {
                List<UserTask> list = userTaskMapper.getByPdfKey(procDefKey);
                for (UserTask userTask : list) {
                    String taskDefKey = userTask.getTaskdefkey();
                    String ids = request.getParameter(taskDefKey + "_id");
                    String names = request.getParameter(taskDefKey + "_name");
                    String taskType = request.getParameter(taskDefKey + "_taskType");
                    userTask.setTasktype(taskType);
                    userTask.setCandidateName(names);
                    userTask.setCandidateIds(ids);
                    userTaskMapper.updateByPrimaryKeySelective(userTask);
                }
                return;
            }
            throw new AppException(Error.PARAMS_ERROR);
        }catch (Exception e)
        {
            throw e;
        }
    }
}
