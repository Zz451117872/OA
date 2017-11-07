package com.example.OA.service.activiti;

import com.example.OA.dao.LeaveMapper;
import com.example.OA.dao.UserMapper;
import com.example.OA.model.Leave;
import com.example.OA.model.activiti.TaskBean;
import com.example.OA.mvc.common.ServerResponse;
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


import java.util.List;
import java.util.Map;

/**
 * Created by aa on 2017/11/7.
 */
@Service
public class LeaveWorkflowService {

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

    public ServerResponse startWorkflow(Leave leave, Map<String, Object> variables) {
        leaveMapper.insertSelective(leave);
        logger.info("save entity: {}", leave);

        java.lang.String businessKey = leave.getId()+"";
        ProcessInstance processInstance = null;
        try {
            // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
            identityService.setAuthenticatedUserId(leave.getApplication()+"");

            processInstance = runtimeService.startProcessInstanceByKey("leave", businessKey, variables);
            String processInstanceId = processInstance.getId();
            leave.setProcessinstanceid(processInstanceId);

            logger.info("start process of {key={}, bkey={}, pid={}, variables={}}", new Object[]{"leave", businessKey, processInstanceId, variables});
            return ServerResponse.createBySuccess();
        } finally {
            identityService.setAuthenticatedUserId(null);
            return ServerResponse.createByError();
        }
    }

    public List<Leave> findTodoTasks(Integer userId) {

        List<Leave> result = Lists.newArrayList();
        String username = userMapper.getUsernameById(userId);
        List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(username).list();

        // 根据流程的业务ID查询实体并关联
        for (Task task : tasks) {
            String processInstanceId = task.getProcessInstanceId();
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).active().singleResult();
            if (processInstance == null) {
                continue;
            }
            String businessKey = processInstance.getBusinessKey();
            if (businessKey == null) {
                continue;
            }
            Leave leave = leaveMapper.selectByPrimaryKey(Integer.parseInt(businessKey));
            leave.setTask(task);
            leave.setProcessInstance(processInstance);
            leave.setProcessDefinition(getProcessDefinition(processInstance.getProcessDefinitionId()));
            result.add(leave);
        }
        return result;
    }



    public List<Leave> findRunningProcessInstaces() {

        List<Leave> result = Lists.newArrayList();
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()//
                .processDefinitionKey("leave").active().orderByProcessDefinitionId().desc().list();

        for(ProcessInstance instance : processInstances)
        {
            String businessKey = instance.getBusinessKey();
            if (businessKey == null) {
                continue;
            }
            Leave leave = leaveMapper.selectByPrimaryKey(Integer.parseInt(businessKey));
            leave.setProcessInstance(instance);
            leave.setProcessDefinition(getProcessDefinition(instance.getProcessDefinitionId()));
            result.add(leave);

            // 设置当前任务信息
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(instance.getId()).active().orderByTaskCreateTime().desc().listPage(0, 1);
            leave.setTask(tasks.get(0));
        }
        return result;
    }

    public List<Leave> findFinishedProcessInstaces() {

        List<Leave> result = Lists.newArrayList();
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey("leave").finished().orderByProcessInstanceEndTime().desc().list();

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
}
