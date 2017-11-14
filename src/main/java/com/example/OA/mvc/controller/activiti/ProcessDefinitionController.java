package com.example.OA.mvc.controller.activiti;

import com.example.OA.model.activiti.*;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.activiti.ProcessDefinitionService;
import com.example.OA.service.activiti.WorkflowService;
import com.google.common.collect.Lists;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aa on 2017/11/6.
 */
@RestController
@RequestMapping("process")
public class ProcessDefinitionController extends CommonController{

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

    protected static Map<String, ProcessDefinition> PROCESS_DEFINITION_CACHE = new HashMap<String, ProcessDefinition>();

    //部署流程定义
    @RequestMapping(value = "deploy_pdf",method = RequestMethod.POST)
    public String deploymentProcessDefinition(@RequestParam(value = "processName",required = true) String processName,
                                              @RequestParam(value = "deploymentName",required = false,defaultValue = "呵呵") String deploymentName) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return processDefinitionService.deploymentProcessDefinition(processName,deploymentName);
    }

    //获取所有流程定义
    @RequestMapping(value = "all_pdf",method = RequestMethod.POST)
    public List<ProcessDefinitionBean> getAllProcessDefinition() {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return processDefinitionService.getAllProcessDefinition();
    }

    //获取单个流程定义
    @RequestMapping(value = "get_pdf",method = RequestMethod.POST)
    public ProcessDefinitionBean get(@RequestParam(value = "processId",required = true) String processId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return processDefinitionService.get(processId);
    }

    //删除流程定义
    @RequestMapping(value = "delete_pdf",method = RequestMethod.POST)
    public void deleteProcessDefinition(@RequestParam(value = "processId",required = true) String processId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        processDefinitionService.deleteProcessDefinition(processId);
        return;
    }

    //删除所有流程定义
    @RequestMapping(value = "delete_all_pdf",method = RequestMethod.POST)
    public void deleteAllProcessDefinition() {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        processDefinitionService.deleteAllProcessDefinition();
        return;
    }

    //加载资源文件 通过流程定义，中文显示不出，不知原因
    @RequestMapping(value = "load_by_pdf",method = RequestMethod.POST)
    public void loadByProcessDefinition(@RequestParam(value = "processDefinitionId",required = true) String processDefinitionId,
                                        @RequestParam(value = "resourceType",required = false ,defaultValue = "image")String resourceType,
                                        HttpServletResponse response) {
           try{
               Subject subject = SecurityUtils.getSubject();
               if(!subject.isAuthenticated()) {
                   throw new AppException(Error.UN_AUTHORIZATION);
               }
               ProcessDefinition processDefinition = repositoryService//
                       .createProcessDefinitionQuery()//
                       .processDefinitionId(processDefinitionId)//
                       .singleResult();
               String resourceName = "";
               if (resourceType.equals("image")) {
                   resourceName = processDefinition.getDiagramResourceName();
               } else if (resourceType.equals("xml")) {
                   resourceName = processDefinition.getResourceName();
               }
               InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);

               byte[] b = new byte[1024];
               int len = -1;
               while ((len = resourceAsStream.read(b,0,1024)) != -1) {
                   response.getOutputStream().write(b,0,len);
               }
               return;
           }catch (Exception e)
           {
               throw new AppException(Error.UNKNOW_EXCEPTION,e.getMessage());
           }
    }

    //加载资源文件 通过 流程实例，中文显示不出，不知原因
    @RequestMapping(value = "load_by_pin",method = RequestMethod.POST)
    public void loadByProcessInstance(
            @RequestParam(value = "processInstanceId",required = true) String processInstanceId,
            @RequestParam(value = "resourceType",required = false,defaultValue = "image") String resourceType,
            HttpServletResponse response){
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
            try{
                ProcessInstance processInstance = runtimeService//
                        .createProcessInstanceQuery()//
                        .processInstanceId(processInstanceId)//
                        .singleResult();
                ProcessDefinition processDefinition = repositoryService//
                        .createProcessDefinitionQuery()//
                        .processDefinitionId(processInstance.getProcessDefinitionId())//
                        .singleResult();

                String resourceName = "";
                if (resourceType.equals("image")) {
                    resourceName = processDefinition.getDiagramResourceName();
                } else if (resourceType.equals("xml")) {
                    resourceName = processDefinition.getResourceName();
                }
                InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
                byte[] b = new byte[1024];
                int len = -1;
                while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
                    response.getOutputStream().write(b, 0, len);
                }
                return;
            }catch (Exception e)
            {
                throw new AppException(Error.UNKNOW_EXCEPTION,e.getMessage());
            }
    }

    //读取流程图,中文不能显示，不知原因
    @RequestMapping(value = "read_resource",method = RequestMethod.POST)
    public void readResource(
            @RequestParam(value = "executionId",required = true) String executionId,
            HttpServletResponse response) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
            try{
                ProcessInstance processInstance = runtimeService//
                        .createProcessInstanceQuery()//
                        .processInstanceId(executionId).singleResult();
                BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
                List<String> activeActivityIds = runtimeService.getActiveActivityIds(executionId);

                ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
                InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", activeActivityIds);

                byte[] b = new byte[1024];
                int len;
                while ((len = imageStream.read(b, 0, 1024)) != -1) {
                    response.getOutputStream().write(b, 0, len);
                }
                return;
            }catch (Exception e)
            {
                throw new AppException(Error.UNKNOW_EXCEPTION,e.getMessage());
            }
    }

    //修改流程定义的状态
    @RequestMapping(value = "update_pdf_status",method = RequestMethod.POST)
    public void updateState(@RequestParam(value = "state",required = true) String state,
                            @RequestParam(value = "processDefinitionId",required = true) String processDefinitionId) {
            try {
                Subject subject = SecurityUtils.getSubject();
                if(!subject.isAuthenticated()) {
                    throw new AppException(Error.UN_AUTHORIZATION);
                }
                if (state.equals("active")) {
                    repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
                    return;
                } else if (state.equals("suspend")) {
                    repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
                    return;
                }
                throw new AppException(Error.PARAMS_ERROR);
            } catch (Exception e) {
                throw new AppException(Error.UNKNOW_EXCEPTION,e.getMessage());
            }
    }

    //通过流程实例 得到 流程定义
    private ProcessDefinition getProcessDefinition(String processDefinitionId) {
       if(processDefinitionId != null) {
           try{
               ProcessDefinition processDefinition = PROCESS_DEFINITION_CACHE.get(processDefinitionId);
               if (processDefinition == null) {
                   processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
                   PROCESS_DEFINITION_CACHE.put(processDefinitionId, processDefinition);
               }
               return processDefinition;
           }catch (Exception e)
           {
               throw new AppException(Error.UNKNOW_EXCEPTION,e.getMessage());
           }
       }
       return null;
    }

    //加载流程定义
    @RequestMapping(value = "list_processDefinition",method = RequestMethod.POST)
    public List<ProcessDefinitionBean> listProcessDefinition() {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        try{
            List<ProcessDefinition> processDefinitionList = repositoryService//
                .createProcessDefinitionQuery()//
                .orderByDeploymentId().desc().list();
            List<ProcessDefinitionBean> result = Lists.newArrayList();

           for (ProcessDefinition processDefinition : processDefinitionList) {
               ProcessDefinitionBean pd = new ProcessDefinitionBean();
               String deploymentId = processDefinition.getDeploymentId();
               Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
               //封装到ProcessDefinitionEntity中
               pd.setId(processDefinition.getId());
               pd.setName(processDefinition.getName());
               pd.setKey(processDefinition.getKey());
               pd.setDeploymentId(processDefinition.getDeploymentId());
               pd.setVersion(processDefinition.getVersion());
               pd.setResourceName(processDefinition.getResourceName());
               pd.setDiagramResourceName(processDefinition.getDiagramResourceName());
               pd.setDeploymentTime(deployment.getDeploymentTime());
               pd.setSuspended(processDefinition.isSuspended());
               result.add(pd);
           }
           return result;
       }catch (Exception e)
       {
           throw new AppException(Error.UNKNOW_EXCEPTION,e.getMessage());
       }
    }



    //设置流程实例的状态
    @RequestMapping(value = "update_pin_status",method = RequestMethod.POST)
    public void updateProcessStatusByProInstanceId(
            @RequestParam(value = "status", required = true) String status,
            @RequestParam(value = "processInstanceId", required = true) String processInstanceId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if (status.equals("active")) {
            processDefinitionService.activateProcessInstance(processInstanceId);
            return;
        } else if (status.equals("suspend")) {
            processDefinitionService.suspendProcessInstance(processInstanceId);
            return;
        }
        throw new AppException(Error.PARAMS_ERROR);
    }




}
