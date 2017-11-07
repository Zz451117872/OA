package com.example.OA.mvc.controller.activiti;

import com.example.OA.model.User;
import com.example.OA.model.activiti.ProcessDefinitionBean;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.CommonService;
import com.example.OA.service.activiti.WorkflowProcessDefinitionService;
import com.example.OA.service.activiti.WorkflowTraceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aa on 2017/11/6.
 */
@RestController
@RequestMapping("process")
public class ActivitiController extends CommonController{

    @Autowired
    WorkflowProcessDefinitionService workflowProcessDefinitionService;

    @Autowired
    WorkflowTraceService workflowTraceService;

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

    @RequestMapping(value = "deploy_pdf",method = RequestMethod.POST)
    public String deploymentProcessDefinition(String processName,String deploymentName) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(processName != null && deploymentName != null)
        {
            return workflowProcessDefinitionService.deploymentProcessDefinition(processName,deploymentName);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }


    @RequestMapping(value = "all_pdf",method = RequestMethod.POST)
    public List<ProcessDefinitionBean> getAllProcessDefinition() {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
            return workflowProcessDefinitionService.getAllProcessDefinition();
    }

    @RequestMapping(value = "get_pdf",method = RequestMethod.POST)
    public ProcessDefinitionBean get(String processId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(processId != null )
        {
            return workflowProcessDefinitionService.get(processId);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    @RequestMapping(value = "delete_pdf",method = RequestMethod.POST)
    public ServerResponse deleteProcessDefinition(String processId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(processId != null )
        {
            return workflowProcessDefinitionService.deleteProcessDefinition(processId);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    @RequestMapping(value = "delete_all_pdf",method = RequestMethod.POST)
    public void deleteAllProcessDefinition() {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }

        workflowProcessDefinitionService.deleteAllProcessDefinition();
    }

    @RequestMapping(value = "load_pdf_by_id",method = RequestMethod.POST)
    public void loadByProcessDefinition(String processDefinitionId, String resourceType, HttpServletResponse response) throws IOException {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
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
    }

    @RequestMapping(value = "load_pdf_by_piid",method = RequestMethod.POST)
    public void loadByProcessInstance(String processInstanceId,String resourceType,HttpServletResponse response) throws IOException {
        InputStream resourceAsStream = null;
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId())
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
    }

    @RequestMapping(value = "trace_process",method = RequestMethod.POST)
    public List<Map<String, Object>> traceProcess(String processInstanceId)throws Exception
    {
        return workflowTraceService.traceProcess(processInstanceId);
    }

    @RequestMapping(value = "read_resource",method = RequestMethod.POST)
    public void readResource(String executionId,HttpServletResponse response) throws IOException {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(executionId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(executionId);

        processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);

        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", activeActivityIds);

        byte[] b = new byte[1024];
        int len;
        while ((len = imageStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }

    @RequestMapping(value = "convert_to_model",method = RequestMethod.POST)
    public ServerResponse convertToModel(String processDefinitionId) throws UnsupportedEncodingException, XMLStreamException {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
                processDefinition.getResourceName());
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

        BpmnJsonConverter converter = new BpmnJsonConverter();
        com.fasterxml.jackson.databind.node.ObjectNode modelNode = converter.convertToJson(bpmnModel);
        Model modelData = repositoryService.newModel();
        modelData.setKey(processDefinition.getKey());
        modelData.setName(processDefinition.getResourceName());
        modelData.setCategory(processDefinition.getDeploymentId());

        ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processDefinition.getName());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, processDefinition.getDescription());
        modelData.setMetaInfo(modelObjectNode.toString());

        repositoryService.saveModel(modelData);

        repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));

        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "todo_list",method = RequestMethod.POST)
    public List<Map<String, Object>> todoList()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        List<Map<String, Object>> result = Lists.newArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        // 个人任务
        List<Task> todoList = taskService.createTaskQuery().taskAssignee(user.getUsername()).active().list();
        for (Task task : todoList) {
            String processDefinitionId = task.getProcessDefinitionId();
            ProcessDefinition processDefinition = getProcessDefinition(processDefinitionId);

            Map<String, Object> singleTask = packageTaskInfo(sdf, task, processDefinition);
            singleTask.put("status", "todo");
            result.add(singleTask);
        }

        // 组任务
        List<Task> toClaimList = taskService.createTaskQuery().taskCandidateUser(user.getUsername()).active().list();
        for (Task task : toClaimList) {
            String processDefinitionId = task.getProcessDefinitionId();
            ProcessDefinition processDefinition = getProcessDefinition(processDefinitionId);

            Map<String, Object> singleTask = packageTaskInfo(sdf, task, processDefinition);
            singleTask.put("status", "claim");
            result.add(singleTask);
        }
        return result;
    }

    //驳回任务
    @RequestMapping(value = "returnback_task",method = RequestMethod.POST)
    public ServerResponse returnBackTask(String taskId) {
        if(taskId != null)
        {
            try{
                Map<String,Object> variables;
                //取得 当前任务
                HistoricTaskInstance targetTask = historyService//
                        .createHistoricTaskInstanceQuery()//
                        .taskId(taskId).singleResult();
                //取得流程实例
                ProcessInstance processInstance = runtimeService//
                        .createProcessInstanceQuery()//
                        .processInstanceId(targetTask.getProcessInstanceId())//
                        .singleResult();

                if(processInstance != null)
                {
                    throw new AppException(Error.NO_EXISTS,"process is end");
                }

                variables = processInstance.getProcessVariables();
                //取得流程定义
                ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)//
                        ((RepositoryServiceImpl)//
                                repositoryService).getDeployedProcessDefinition(//
                                targetTask.getProcessDefinitionId());

                if( processDefinitionEntity == null)
                {
                    throw new AppException(Error.NO_EXISTS,"process not selected");
                }
                //取得上一步活动
                ActivityImpl targetActiviti = ((ProcessDefinitionImpl)processDefinitionEntity)//
                        .findActivity(targetTask.getTaskDefinitionKey());

                //清除当前活动的出口
                List<PvmTransition> inPvmTransitions = targetActiviti.getIncomingTransitions();
                List<PvmTransition> pvmTransitions = Lists.newArrayList();
                List<PvmTransition> outPvmTransitions = targetActiviti.getOutgoingTransitions();

                for(PvmTransition pvmTransition : outPvmTransitions)
                {
                    pvmTransitions.add(pvmTransition);
                }
                outPvmTransitions.clear();

                //建立新出口
                List<TransitionImpl> newTransitions = Lists.newArrayList();
                for(PvmTransition pvmTransition : inPvmTransitions)
                {
                    PvmActivity pvmActivity = pvmTransition.getSource();
                    ActivityImpl activity = ((ProcessDefinitionImpl)processDefinitionEntity)//
                            .findActivity(pvmActivity.getId());

                    TransitionImpl newTransition = targetActiviti.createOutgoingTransition();
                    newTransition.setDestination(activity);
                    newTransitions.add(newTransition);
                }

                //完成任务
                List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId())
                        .taskDefinitionKey(targetTask.getTaskDefinitionKey()).list();
                for(Task task : tasks)
                {
                    taskService.complete(task.getId(),variables);
                    historyService.deleteHistoricTaskInstance(task.getId());
                }

                //恢复方向
                for(TransitionImpl transition : newTransitions)
                {
                    targetActiviti.getOutgoingTransitions().remove(transition);
                }
                for(PvmTransition pvmTransition : pvmTransitions)
                {
                    outPvmTransitions.add(pvmTransition);
                }
                return ServerResponse.createBySuccess();
            }catch (Exception e)
            {
                throw e;
            }
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "update_state",method = RequestMethod.POST)
    public ServerResponse updateState(String state,String processDefinitionId)
    {
        if (state.equals("active")) {
            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
            return ServerResponse.createBySuccess();
        } else if (state.equals("suspend")) {
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    private ProcessDefinition getProcessDefinition(String processDefinitionId) {
        ProcessDefinition processDefinition = PROCESS_DEFINITION_CACHE.get(processDefinitionId);
        if (processDefinition == null) {
            processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
            PROCESS_DEFINITION_CACHE.put(processDefinitionId, processDefinition);
        }
        return processDefinition;
    }

    private Map<String,Object> packageTaskInfo(SimpleDateFormat sdf, Task task, ProcessDefinition processDefinition) {
        Map<String, Object> singleTask = new HashMap<String, Object>();
        singleTask.put("id", task.getId());
        singleTask.put("name", task.getName());
        singleTask.put("createTime", sdf.format(task.getCreateTime()));
        singleTask.put("pdname", processDefinition.getName());
        singleTask.put("pdversion", processDefinition.getVersion());
        singleTask.put("pid", task.getProcessInstanceId());
        return singleTask;
    }
}
