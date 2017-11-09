package com.example.OA.service.activiti;

import com.example.OA.model.activiti.ProcessDefinitionBean;
import com.example.OA.model.activiti.TaskBean;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.CommonService;
import com.google.common.collect.Lists;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by aa on 2017/11/7.
 */
@Service
public class WorkflowProcessDefinitionService extends CommonService{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    HistoryService historyService;

    @Autowired
    TaskService taskService;


    public String deploymentProcessDefinition(String processName,String deploymentName) {
        try{
            DeploymentBuilder deploymentBuilder =  repositoryService.createDeployment().name(deploymentName);
            deploymentBuilder = deploymentBuilder.addClasspathResource("processes/"+processName+".bpmn");
            Deployment deployment = deploymentBuilder.deploy();
            if(deployment != null)
            {
                return deployment.getId();
            }
            throw new AppException(Error.UNKNOW_EXCEPTION,"deploymentProcessDefinition error");
        }catch (Exception e)
        {
            throw e;
        }
    }

    public List<ProcessDefinitionBean> getAllProcessDefinition() {
        try{
            List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().list();
            if(processDefinitionList != null && !processDefinitionList.isEmpty())
            {
                List<ProcessDefinitionBean> result = Lists.newArrayList();
                for(ProcessDefinition processDefinition : processDefinitionList)
                {
                    result.add(convertProcessDefinitionBean(processDefinition));
                }
                return result;
            }
            return null;
        }catch (Exception e)
        {
            throw e;
        }
    }

    public ProcessDefinitionBean get(String processId) {
        try{
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processId).singleResult();
            if(processDefinition != null)
            {
                return convertProcessDefinitionBean(processDefinition);
            }
            return null;
        }catch (Exception e)
        {
            throw e;
        }
    }

    public ServerResponse deleteProcessDefinition(String processId) {
        logger.info("invoke->deleteProcessDefinition");
        try{
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processId).singleResult();
            if(processDefinition != null)
            {
                repositoryService.deleteDeployment(processDefinition.getDeploymentId(),true);
                logger.info("deleteDeployment:"+processDefinition.getDeploymentId());
                return ServerResponse.createBySuccess();
            }else {
                logger.info(processId+"->没有对应的部署对象");
                return ServerResponse.createByError();
            }
        }catch (Exception e)
        {
            throw e;
        }
    }


    public void deleteAllProcessDefinition() {
        logger.info("invoke->deleteAllProcessDefinition");
        try {
            List<Deployment> deployments =  repositoryService.createDeploymentQuery().list();
            if(deployments != null && !deployments.isEmpty())
            {
                for (Deployment deployment : deployments)
                {
                    repositoryService.deleteDeployment(deployment.getId(),true);
                    logger.info("deleteDeployment :->"+deployment.getId());
                }
            }
        }catch (Exception e)
        {
            throw e;
        }
    }


    public List<TaskBean> todoList(String username) {
        if(username != null)
        {
            try{
                List<TaskBean> result = Lists.newArrayList();
                // 个人任务
                List<Task> todoList = taskService.createTaskQuery().taskAssignee(username).active().list();
                for (Task task : todoList) {
                    TaskBean taskBean = convertTask(task);
                    taskBean.setStatus("todo");
                    result.add(taskBean);
                }
                // 组任务
                List<Task> toClaimList = taskService.createTaskQuery().taskCandidateUser(username).active().list();
                for (Task task : toClaimList) {
                    TaskBean taskBean = convertTask(task);
                    taskBean.setStatus("clima");
                    result.add(taskBean);
                }
                return result;
            }catch (Exception e)
            {
                throw e;
            }
        }
        throw new AppException(Error.PARAMS_ERROR);
    }
}
