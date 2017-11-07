package com.example.OA.service.activiti;

import com.example.OA.model.activiti.ProcessDefinitionBean;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.google.common.collect.Lists;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by aa on 2017/11/7.
 */
@Service
public class WorkflowProcessDefinitionService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    HistoryService historyService;

    public ServerResponse startProcessDefinition(String processDefinitionKey)
    {
        if(processDefinitionKey != null)
        {
            try{
                ProcessInstance instance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
                if( instance != null)
                {
                    return ServerResponse.createBySuccess();
                }
                return ServerResponse.createByError();
            }catch (Exception e)
            {
                throw e;
            }
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public String deploymentProcessDefinition(String processName,String deploymentName) {
        try{
            DeploymentBuilder deploymentBuilder =  repositoryService.createDeployment().name(deploymentName);
            deploymentBuilder = deploymentBuilder.addClasspathResource("processes/processName.bpmn");
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

    private ProcessDefinitionBean convertProcessDefinitionBean(ProcessDefinition processDefinition) {
        if(processDefinition != null)
        {
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(processDefinition.getDeploymentId()).singleResult();
            ProcessDefinitionBean processDefinitionBean = new ProcessDefinitionBean();
            processDefinitionBean.setId(processDefinition.getId());
            processDefinitionBean.setKey(processDefinition.getKey());
            processDefinitionBean.setDevloyment_id(processDefinition.getDeploymentId());
            processDefinitionBean.setPro_defi_name(processDefinition.getName());
            processDefinitionBean.setPro_devl_name(deployment.getName());
            processDefinitionBean.setPro_devl_time(deployment.getDeploymentTime());
            processDefinitionBean.setVersion(processDefinition.getVersion()+"");
            return processDefinitionBean;
        }
        throw new AppException(Error.PARAMS_ERROR,"convertProcessDefinitionBean error");
    }

    public ServerResponse deleteProcessDefinition(String processId) {
        logger.info("invoke->deleteProcessDefinition");
        try{
            repositoryService.deleteDeployment(processId,true);
            logger.info("deleteDeployment:"+processId);
            return ServerResponse.createBySuccess();
        }catch (Exception e)
        {
            throw e;
        }
    }


    public void deleteAllProcessDefinition() {
        logger.info("invoke->deleteAllProcessDefinition");
        try{
            List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().list();
            if(processDefinitionList != null && !processDefinitionList.isEmpty())
            {
                for(ProcessDefinition processDefinition : processDefinitionList)
                {
                    repositoryService.deleteDeployment(processDefinition.getDeploymentId(),true);
                    logger.info("deleteDeployment:"+processDefinition.getDeploymentId());
                }
            }
        }catch (Exception e)
        {
            throw e;
        }
    }


    public ProcessDefinition findProcessDefinitionByPid(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        ProcessDefinition processDefinition = findProcessDefinition(processDefinitionId);
        return processDefinition;
    }


    public ProcessDefinition findProcessDefinition(String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        return processDefinition;
    }
}
