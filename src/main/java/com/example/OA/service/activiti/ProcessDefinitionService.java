package com.example.OA.service.activiti;

import com.example.OA.model.activiti.ProcessDefinitionBean;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.CommonService;
import com.google.common.collect.Lists;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by aa on 2017/11/10.
 */
@Service
public class ProcessDefinitionService extends CommonService{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    //部署单个流程定义
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

    //获取所有流程定义
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

    //获取单个流程定义
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

    //删除单个流程定义
    public void deleteProcessDefinition(String processId) {
        logger.info("invoke->deleteProcessDefinition");
        try{
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processId).singleResult();
            if(processDefinition != null)
            {
                repositoryService.deleteDeployment(processDefinition.getDeploymentId(),true);
                logger.info("deleteDeployment:"+processDefinition.getDeploymentId());
                return;
            }else {
                logger.info(processId+"->没有对应的部署对象");
                throw new AppException(Error.NO_EXISTS,"没有对应的部署对象");
            }
        }catch (Exception e)
        {
            throw e;
        }
    }

    //删除所有流程定义
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

    //加载所有运行的流程实例
    public List<ProcessInstance> listRuningProcess(){

        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        List<ProcessInstance> list = processInstanceQuery.orderByProcessInstanceId().desc().list();
        return list;
    }

    //激活流程实例
    public void activateProcessInstance(String processInstanceId) {
        runtimeService.activateProcessInstanceById(processInstanceId);
    }

    //冻结流程实例
    public void suspendProcessInstance(String processInstanceId) {
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

}
