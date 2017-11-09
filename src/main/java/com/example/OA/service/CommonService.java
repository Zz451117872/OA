package com.example.OA.service;

import com.example.OA.dao.UserMapper;
import com.example.OA.model.User;
import com.example.OA.model.activiti.ProcessDefinitionBean;
import com.example.OA.model.activiti.TaskBean;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by aa on 2017/11/1.
 */
@Service
public class CommonService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RepositoryService repositoryService;

    public void verification(Integer userId ) {
        if(userId != null)
        {
            if(userMapper.selectByPrimaryKey(userId) == null)
                throw new AppException(Error.UN_AUTHORIZATION);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public TaskBean convertTask(Task task) {
        TaskBean taskBean = new TaskBean();
        taskBean.setId(task.getId());
        taskBean.setName(task.getName());
        taskBean.setCreateTime(task.getCreateTime());
        taskBean.setAssignee(task.getAssignee());
        taskBean.setExecutionId(task.getExecutionId());
        taskBean.setOwner(task.getOwner());
        taskBean.setProcessInstanceId(task.getProcessInstanceId());
        return taskBean;
    }

    public ProcessDefinitionBean convertProcessDefinitionBean(ProcessDefinition processDefinition) {
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

}
