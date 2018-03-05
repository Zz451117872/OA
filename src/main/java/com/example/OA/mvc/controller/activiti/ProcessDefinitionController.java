package com.example.OA.mvc.controller.activiti;

import com.example.OA.model.activiti.*;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.activiti.ProcessDefinitionService;
import com.example.OA.service.activiti.WorkflowService;
import com.github.pagehelper.PageInfo;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Collections;
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

    //部署流程定义 通过 流程定义名称方式
    @RequestMapping(value = "deploy_pdf.do",method = RequestMethod.POST)
    public ServerResponse deploymentProcessDefinition(@RequestParam(value = "processName",required = true) String processName,
                                                      @RequestParam(value = "deploymentName",required = false,defaultValue = "呵呵") String deploymentName) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return processDefinitionService.deploymentProcessDefinition(processName,deploymentName);
    }

    //部署流程定义 通过zip文件方式
    @RequestMapping(value = "deploy_pdf_by_zip.do",method = RequestMethod.POST)
    public ServerResponse deploymentProcessDefinitionByZIP(MultipartHttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        InputStream is = null;
        try {
            is = request.getFile("filename").getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(is != null)
        {

            return processDefinitionService.deploymentProcessDefinitionByZIP(is);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //部署所有流程定义
    @RequestMapping(value = "deploy_all_pdf.do",method = RequestMethod.POST)
    public ServerResponse deploymentAllProcessDefinition() {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return processDefinitionService.deploymentAll();

    }

    //获取所有流程定义
    @RequestMapping(value = "all_pdf.do",method = RequestMethod.POST)
    public PageInfo getAllProcessDefinition(@RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize",required = false,defaultValue = "3") Integer pageSize) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return processDefinitionService.getAllProcessDefinition(pageNum,pageSize);
    }

    //获取单个流程定义
    @RequestMapping(value = "get_pdf.do",method = RequestMethod.POST)
    public ProcessDefinitionBean get(@RequestParam(value = "processDefinitionId.do",required = true) String processId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return processDefinitionService.get(processId);
    }

    //删除流程定义
    @RequestMapping(value = "delete_pdf.do",method = RequestMethod.POST)
    public void deleteProcessDefinition(@RequestParam(value = "processDefinitionId",required = true) String processId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        processDefinitionService.deleteProcessDefinition(processId);
        return;
    }

    //删除所有流程定义
    @RequestMapping(value = "delete_all_pdf.do",method = RequestMethod.POST)
    public void deleteAllProcessDefinition() {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        processDefinitionService.deleteAllProcessDefinition();
        return;
    }

    //通过流程定义 获取资源图
    @RequestMapping(value = "get_resource_map.do",method = RequestMethod.POST)
    public ServerResponse getResourceMap(String processDefinitionId)
    {
        try {
            Subject subject = SecurityUtils.getSubject();
            if (!subject.isAuthenticated()) {
                throw new AppException(Error.UN_AUTHORIZATION);
            }
            ProcessDefinition processDefinition = repositoryService//
                    .createProcessDefinitionQuery()//
                    .processDefinitionId(processDefinitionId)//
                    .singleResult();
            String resourceName = processDefinition.getDiagramResourceName();
            InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);

            ByteArrayOutputStream baos =new ByteArrayOutputStream();

            int len = -1;
            byte[] bytes = new byte[1024];
            while ((len = resourceAsStream.read(bytes,0,bytes.length)) != -1)
            {
                baos.write(bytes,0,len);
            }

            byte[] result = baos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            return ServerResponse.createBySuccess(encoder.encode(result));

        }catch (IOException e)
        {
            e.printStackTrace();
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }


    //读取流程图
    @RequestMapping(value = "getFlowChart.do",method = RequestMethod.POST)
    public ServerResponse getFlowChart(@RequestParam(value = "executionId",required = true) String executionId) {
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
                InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", activeActivityIds,Collections.<String>emptyList(), "宋体", "宋体", "宋体", null, 1.0);

                ByteArrayOutputStream baos =new ByteArrayOutputStream();

                int len = -1;
                byte[] bytes = new byte[1024];
                while ((len = imageStream.read(bytes,0,bytes.length)) != -1)
                {
                    baos.write(bytes,0,len);
                }

                byte[] result = baos.toByteArray();

                BASE64Encoder encoder = new BASE64Encoder();
                return ServerResponse.createBySuccess(encoder.encode(result));
            }catch (Exception e)
            {
                e.printStackTrace();
                throw new AppException(Error.UNKNOW_EXCEPTION,e.getMessage());
            }
    }

    //修改流程定义的状态
    @RequestMapping(value = "update_pdf_status.do",method = RequestMethod.POST)
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


    //设置流程实例的状态
    @RequestMapping(value = "update_pin_status.do",method = RequestMethod.POST)
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

    //查询所有的流程定义名
    @RequestMapping(value = "all_pdf_names.do",method = RequestMethod.POST)
    public List<String> getAllProcessDefinetionName()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return processDefinitionService.getAllProcessDefinetionName();
    }

    //查询所有的流程定义key
    @RequestMapping(value = "all_pdf_key.do",method = RequestMethod.POST)
    public List<String> getAllProcessDefinetionKey()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return processDefinitionService.getAllProcessDefinetionKey();
    }

}
