package com.example.OA.service.activiti;

import com.example.OA.dao.LeaveMapper;
import com.example.OA.dao.UserMapper;
import com.example.OA.model.Leave;
import com.example.OA.model.User;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by aa on 2017/11/3.
 */
@Service
public class LeaveService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RuntimeService runtimeService ;

    @Autowired
    LeaveMapper leaveMapper;

    @Autowired
    TaskService taskService;

    @Autowired
    UserMapper userMapper;


    public Map<String,Leave> application(Leave leave,String key) {
        leave.setCreateTime(new Date());
        leave.setStatus(Const.LeaveStatus.APPLICATION.getCode());
        leaveMapper.insertSelective(leave);

        logger.info("leaveMapper.insertSelective(leave)");

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(key);
        logger.info("流程实例启动成功");
        Task task = taskService.createTaskQuery()
                .processInstanceId(instance.getProcessInstanceId())
                .singleResult();
        logger.info("task:"+task);
        Map<String,Leave> result = Maps.newHashMap();
        if(task != null)
        {
            taskService.setVariable(task.getId(),"leave",leave.getId());
            taskService.complete(task.getId());
            result.put(instance.getId(),leave);
            logger.info("taskService.setVariable:"+leave.getId());
            return result;
        }
        logger.info("task not existed");
        throw new AppException(Error.NO_EXISTS,"task not existed");
    }


    @Transactional
    public Integer cancleApplication(Integer userId, String processInstanceId) {
        if (userId != null && processInstanceId != null) {
            ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (instance != null) {
                Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
                if (task != null) {
                    Integer leaveId = (Integer) taskService.getVariable(task.getId(), "leave");
                    if (leaveId != null) {
                        leaveMapper.updateLeaveStatus(leaveId, Const.LeaveStatus.CANCELED.getCode());

                        runtimeService.deleteProcessInstance(processInstanceId, "干地就是你");
                        return leaveId;
                    }
                    throw new AppException(Error.NO_EXISTS, "get variables error");
                }
                throw new AppException(Error.NO_EXISTS, "task not existed or end");
            }
            throw new AppException(Error.NO_EXISTS, "instance not existed or end");
        }
        throw new AppException(Error.PARAMS_ERROR, "param error");
    }

    public List<Leave> myApplication(Integer userId) {

        if(userId != null)
        {
            return leaveMapper.myApplicationEorStatus(userId,Const.LeaveStatus.CLOSED.getCode());
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public List<Leave> myApplicationHistory(Integer userId) {
        if(userId != null)
        {
            return leaveMapper.getByApplicationAndStatus(userId,Const.LeaveStatus.CLOSED.getCode());
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public List<Leave> getAllByStatus(Integer status) {
        try{
            Const.LeaveStatus.codeof(status);
        }catch (Exception e)
        {
            throw new AppException(Error.PARAMS_ERROR,"status error");
        }
        return leaveMapper.getAllByStatus(status);
    }

    public Leave getById(Integer leaveId) {
       if(leaveId != null)
       {
           return leaveMapper.selectByPrimaryKey(leaveId);
       }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public List<Leave> getByApplicationIdOrName(Integer userId, String username) {

        if(userId == null)
            {
                User user = userMapper.getByUsername(username);
                if(user != null)
                {
                    userId = user.getId();
                }
            }
        if(userId != null) {
            return leaveMapper.getByApplication(userId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public Map<String,Leave> needIDispose(Integer userId,String definitionKey) {

        if(userId != null && definitionKey != null)
        {
            User user = userMapper.selectByPrimaryKey(userId);
            if(user == null) throw new AppException(Error.NO_EXISTS,"user not existed");

            List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(definitionKey).list();

            Map<String,Leave> result = Maps.newHashMap();
            if(tasks != null && !tasks.isEmpty())
            {
                logger.info("查到"+tasks.size()+"个任务");
                for(int i=0; i<tasks.size(); i++)
                {
                    Task task = tasks.get(i);
                    if(task.getAssignee().equals(user.getUsername()))
                    {
                        Integer leaveId= (Integer) taskService.getVariable(task.getId(),"leave");
                       if(leaveId != null)
                       {
                           Leave leave = leaveMapper.selectByPrimaryKey(leaveId);

                           result.put(task.getId(), leave);
                           logger.info("从任务中获得变量："+leaveId);
                       }else{
                           throw new AppException(Error.NO_EXISTS,"taskService.getVariable error");
                       }
                    }
                }
                return result;
            }
            logger.info("查到"+0+"个任务");
           return result;
        }
        throw new AppException(Error.PARAMS_ERROR);
    }
}
