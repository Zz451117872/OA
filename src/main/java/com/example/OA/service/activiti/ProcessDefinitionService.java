package com.example.OA.service.activiti;

import com.example.OA.model.activiti.ProcessDefinitionBean;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.CommonService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

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

    //获取所有流程定义的名称
    public List<String> getAllProcessDefinetionName() {
        try{
            String path = "F:\\IDEAPATH\\OA\\src\\main\\resources\\processes" ;
            File file = new File(path);
            if(file.exists()) {
                List<String> pdf_names = Lists.newArrayList();
                File[] pdfs = file.listFiles();
                for (int i = 0; i < pdfs.length; i++) {
                    File pdf = pdfs[i];
                    if (pdf.getName().endsWith(".bpmn")) {
                        String name = pdf.getName();
                        pdf_names.add(name.substring(0,name.indexOf(".")));
                    }
                }
                return pdf_names;
            }
            return null;
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    //部署所有流程定义
    public ServerResponse deploymentAll() {
        try{
            String path = "F:\\IDEAPATH\\OA\\src\\main\\resources\\processes" ;
            File file = new File(path);

            DeploymentBuilder deploymentBuilder =  repositoryService.createDeployment().name("auto_deploy_all");
            if(file.exists())
            {
                File[] pdfs = file.listFiles();
                for(int i=0; i<pdfs.length; i++) {
                    File pdf = pdfs[i];
                    if (pdf.getName().endsWith(".bpmn")) {
                        System.out.println("部署pdf:" + pdf.getName());
                        deploymentBuilder = deploymentBuilder.addClasspathResource("processes/" + pdf.getName());
                    }
                }
                Deployment deployment = deploymentBuilder.deploy();
                if(deployment != null)
                {
                    return ServerResponse.createBySuccess();
                }
                throw new AppException(Error.UNKNOW_EXCEPTION,"deploymentProcessDefinition error");
                }
            throw new AppException(Error.PARAMS_ERROR,"path 错误");
        }catch (Exception e)
        {
            throw e;
        }
    }

    //部署单个流程定义：通过流程定义名称
    public ServerResponse deploymentProcessDefinition(String processName,String deploymentName) {
        try{
            DeploymentBuilder deploymentBuilder =  repositoryService.createDeployment().name(deploymentName);
            deploymentBuilder = deploymentBuilder.addClasspathResource("processes/"+processName+".bpmn");
            Deployment deployment = deploymentBuilder.deploy();
            if(deployment != null)
            {
                return ServerResponse.createBySuccess();
            }
            throw new AppException(Error.UNKNOW_EXCEPTION,"deploymentProcessDefinition error");
        }catch (Exception e)
        {
            throw e;
        }
    }

    //部署单个流程定义：通过本地ZIP文件
    public ServerResponse deploymentProcessDefinitionByZIP(InputStream inputStream) {
        try{
            DeploymentBuilder deploymentBuilder =  repositoryService.createDeployment().name("zip deploy");
            deploymentBuilder = deploymentBuilder.addZipInputStream(new ZipInputStream(inputStream));
            Deployment deployment = deploymentBuilder.deploy();
            if(deployment != null)
            {
                return ServerResponse.createBySuccess();
            }
            throw new AppException(Error.UNKNOW_EXCEPTION,"deploymentProcessDefinition error");
        }catch (Exception e)
        {
            throw e;
        }
    }

    //获取所有流程定义
    public PageInfo getAllProcessDefinition( Integer pageNum, Integer pageSize) {
        try{
            List<ProcessDefinition> processDefinitionList = repositoryService//
                    .createProcessDefinitionQuery().latestVersion().list();
            if(processDefinitionList != null && !processDefinitionList.isEmpty())
            {
                List<ProcessDefinitionBean> result = Lists.newArrayList();
                for(ProcessDefinition processDefinition : processDefinitionList)
                {
                    result.add(convertProcessDefinitionBean(processDefinition));
                }
                PageHelper.startPage(pageNum,pageSize);
                return new PageInfo(result);
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
        try{
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processId).singleResult();
            if(processDefinition != null)
            {
                repositoryService.deleteDeployment(processDefinition.getDeploymentId(),true);
                logger.info("deleteDeployment:"+processDefinition.getDeploymentId());
                return;
            }else {
                throw new AppException(Error.TARGET_NO_EXISTS,"没有对应的部署对象");
            }
        }catch (Exception e)
        {
            throw e;
        }
    }

    //删除所有流程定义
    public void deleteAllProcessDefinition() {
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

    //激活流程实例
    public void activateProcessInstance(String processInstanceId) {
        runtimeService.activateProcessInstanceById(processInstanceId);
    }

    //冻结流程实例
    public void suspendProcessInstance(String processInstanceId) {
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }


    public List<String> getAllProcessDefinetionKey() {
        List<ProcessDefinition> processDefinitionList = repositoryService//
                .createProcessDefinitionQuery().latestVersion().list();
        List<String> pdf_keys = Lists.newArrayList();
        for(ProcessDefinition processDefinition : processDefinitionList)
        {
            pdf_keys.add(processDefinition.getKey());
        }
        return pdf_keys;
    }
}
