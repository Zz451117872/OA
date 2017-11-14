package com.example.OA.mvc.controller.activiti;

import com.example.OA.model.User;
import com.example.OA.model.activiti.*;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.activiti.ProcessDefinitionService;
import com.example.OA.service.activiti.WorkflowService;
import com.example.OA.util.ProcessDefinitionCache;
import com.example.OA.util.Variable;
import com.google.common.collect.Lists;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    public String deploymentProcessDefinition(String processName,String deploymentName) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(processName != null && deploymentName != null)
        {
            return processDefinitionService.deploymentProcessDefinition(processName,deploymentName);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
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
    public ProcessDefinitionBean get(String processId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(processId != null )
        {
            return processDefinitionService.get(processId);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    //删除流程定义
    @RequestMapping(value = "delete_pdf",method = RequestMethod.POST)
    public void deleteProcessDefinition(String processId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(processId != null )
        {
            processDefinitionService.deleteProcessDefinition(processId);
            return;
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //删除所有流程定义
    @RequestMapping(value = "delete_all_pdf",method = RequestMethod.POST)
    public void deleteAllProcessDefinition() {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        processDefinitionService.deleteAllProcessDefinition();
    }

    //加载资源文件 通过流程定义，中文显示不出，不知原因
    @RequestMapping(value = "load_by_pdf",method = RequestMethod.POST)
    public void loadByProcessDefinition(String processDefinitionId, String resourceType, HttpServletResponse response) throws IOException {
       if(processDefinitionId != null && resourceType != null)
       {
           try{
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
               throw e;
           }
       }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //加载资源文件 通过 流程实例，中文显示不出，不知原因
    @RequestMapping(value = "load_by_pin",method = RequestMethod.POST)
    public void loadByProcessInstance(String processInstanceId,String resourceType,HttpServletResponse response) throws IOException {
        if(processInstanceId != null && resourceType != null)
        {
            try{
                InputStream resourceAsStream = null;
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
                resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
                byte[] b = new byte[1024];
                int len = -1;
                while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
                    response.getOutputStream().write(b, 0, len);
                }
                return;
            }catch (Exception e)
            {
                throw e;
            }
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //读取流程图,中文不能显示，不知原因
    @RequestMapping(value = "read_resource",method = RequestMethod.POST)
    public void readResource(String executionId,HttpServletResponse response) throws IOException {

        if(executionId != null) {
            try{
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(executionId).singleResult();
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
                throw e;
            }
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //修改流程定义的状态
    @RequestMapping(value = "update_state",method = RequestMethod.POST)
    public void updateState(String state,String processDefinitionId) {
        if(state != null && processDefinitionId != null) {
            try {
                if (state.equals("active")) {
                    repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
                    return;
                } else if (state.equals("suspend")) {
                    repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
                    return;
                }
            } catch (Exception e) {
                throw e;
            }
            throw new AppException(Error.PARAMS_ERROR);
        }
        throw new AppException(Error.PARAMS_ERROR);
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
               throw e;
           }
       }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //加载流程定义
    @RequestMapping(value = "list_processDefinition",method = RequestMethod.POST)
    public List<ProcessDefinitionBean> listProcessDefinition() {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().orderByDeploymentId().desc();
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.list();
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
    }

    //设置流程定义的状态
    @RequestMapping(value = "update_pdf_status",method = RequestMethod.POST)
    public void updateProcessStatusByProDefinitionId(String status, String processDefinitionId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        if (status.equals("active")) {
            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
            return;
        } else if (status.equals("suspend")) {
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
            return;
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //设置流程实例的状态
    @RequestMapping(value = "update_pin_status",method = RequestMethod.POST)
    public void updateProcessStatusByProInstanceId(String status,String processInstanceId) {
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
