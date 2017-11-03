package com.example.OA.service.activiti;

import com.example.OA.dao.DisposeMapper;
import com.example.OA.dao.LeaveMapper;
import com.example.OA.dao.UserMapper;
import com.example.OA.model.Dispose;
import com.example.OA.model.Leave;
import com.example.OA.model.User;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.common.VO.DisposeVO;
import com.example.OA.mvc.common.VO.LeaveVO;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.google.common.collect.Lists;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * Created by aa on 2017/11/3.
 */
@Service
public class DisposeService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DisposeMapper disposeMapper;

    @Autowired
    TaskService taskService ;

    @Autowired
    RuntimeService runtimeService ;

    @Autowired
    UserMapper userMapper;

    @Autowired
    LeaveMapper leaveMapper;

    @Transactional
    public LeaveVO auditApplication(Integer userId, String taskId) {
        if(userId != null && taskId != null)
        {
            User user = userMapper.selectByPrimaryKey(userId);
            if(user == null) throw new AppException(Error.NO_EXISTS);

            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            String processInstanceId = task.getProcessInstanceId();
            if(task != null)
            {
                Integer leaveId = (Integer) taskService.getVariable(task.getId(),"leave");
                Leave leave = leaveMapper.selectByPrimaryKey(leaveId);

                taskService.setVariable(task.getId(),"comment",user.getUsername()+"agreed");
                taskService.complete(task.getId());

                if(leaveId != null)
                {
                    Dispose dispose = new Dispose();
                    dispose.setAuditer(user.getUsername());
                    dispose.setAuditTime(new Date());
                    dispose.setLeaveId(leave.getId());
                    dispose.setInformation("agree");
                    disposeMapper.insertSelective(dispose);

                    leave.setUpdateTime(new Date());
                    leave.setStatus(Const.LeaveStatus.APPROVED.getCode());
                    leaveMapper.updateByPrimaryKeySelective(leave);

                    ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                    if(instance == null)
                    {
                        leave.setUpdateTime(new Date());
                        leave.setStatus(Const.LeaveStatus.CLOSED.getCode());
                        leaveMapper.updateByPrimaryKeySelective(leave);
                    }
                    return convertLeaveVO(leave);
                }
                throw new AppException(Error.NO_EXISTS,"leave not existed");
            }
            throw new AppException(Error.NO_EXISTS,"task not existed");
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public List<LeaveVO> getByAuditer(Integer userId) {
        if(userId != null)
        {
            User user = userMapper.selectByPrimaryKey(userId);
            if(user == null)
                throw new AppException(Error.NO_EXISTS);
            List<Dispose> disposes = disposeMapper.getByAuditer(user.getUsername());
            if(disposes != null && !disposes.isEmpty())
            {
                logger.info("找到"+disposes.size()+"个Dispose");
                List<LeaveVO> leaveVOs = Lists.newArrayList();
                for(int i=0; i<disposes.size(); i++)
                {
                    Dispose dispose = disposes.get(i);
                    Leave leave = leaveMapper.selectByPrimaryKey(dispose.getLeaveId());
                    leaveVOs.add(convertLeaveVO(leave));
                }
                return leaveVOs;
            }
            return Lists.newArrayList();
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public List<User> getAuditerByLeave(Integer leaveId) {
        if(leaveId != null)
        {
            List<String> auditers = disposeMapper.getAuditerNameByLeave(leaveId);
            if(auditers != null && !auditers.isEmpty())
            {
                return userMapper.getByUsernames(auditers);
            }
            return Lists.newArrayList();
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public List<Dispose> getAll() {
        return disposeMapper.getAll();
    }

    private LeaveVO convertLeaveVO(Leave leave) {
        if(leave != null)
        {
            User user = userMapper.selectByPrimaryKey(leave.getApplication());
            List<Dispose> disposes = disposeMapper.getByLeave(leave.getId());

            List<DisposeVO> disposeVOs = Lists.newArrayList();
            if(disposes != null && !disposes.isEmpty())
            {
                for(int i=0; i<disposes.size(); i++)
                {
                    disposeVOs.add(convertDisposeVO(disposes.get(i)));
                }
            }

            LeaveVO leaveVO = new LeaveVO();
            leaveVO.setStatus(leave.getStatus());
            leaveVO.setApplicationId(user.getId());
            leaveVO.setApplicationName(user.getUsername());
            leaveVO.setLeaveNumber(leave.getLeaveNumber());
            leaveVO.setLeaveType(leave.getLeaveType());
            leaveVO.setReason(leave.getReason());
            leaveVO.setDisposes(disposeVOs);
            return leaveVO;

        }
        throw new AppException(Error.UNKNOW_EXCEPTION);
    }

    private DisposeVO convertDisposeVO(Dispose dispose) {
        if(dispose != null)
        {
            User user = userMapper.getByUsername(dispose.getAuditer());

            DisposeVO disposeVO = new DisposeVO();
            disposeVO.setAuditTime(dispose.getAuditTime());
            disposeVO.setAuditerId(user.getId());
            disposeVO.setAuditName(user.getUsername());
            disposeVO.setAuditInformation(dispose.getInformation());
            return disposeVO;
        }
        return null;
    }
}
