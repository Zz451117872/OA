package com.example.OA.mvc.controller.activiti.taskListener.leave;

import com.example.OA.dao.activiti.LeaveMapper;
import com.example.OA.model.activiti.Leave;
import com.example.OA.mvc.common.Const;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 调整请假内容处理器
 */
@Component
@Transactional
public class AfterModifyApplyContentProcessor implements TaskListener {

    private static final long serialVersionUID = 1L;

    @Autowired
    LeaveMapper leaveMapper;

    @Autowired
    RuntimeService runtimeService;

    /*
    这个方法是在“complete”时调用的，还有“create,assignment,delete"等
     */
    public void notify(DelegateTask delegateTask) {

        boolean reApply = (Boolean)delegateTask.getVariable("reApply");

        String processInstanceId = delegateTask.getProcessInstanceId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        Leave leave = leaveMapper.selectByPrimaryKey(Integer.parseInt(processInstance.getBusinessKey()));
        if(reApply) {
            leave.setLeaveType((String) delegateTask.getVariable("leaveType"));//这些都是前台传进来的参数，完成任务时被设置进来
            leave.setStartTime((Date) delegateTask.getVariable("startTime"));
            leave.setEndTime((Date) delegateTask.getVariable("endTime"));
            leave.setReason((String) delegateTask.getVariable("reason"));
            leave.setLeaveNumber((Integer) delegateTask.getVariable("leaveNumber"));
            leave.setUpdateTime(new Date());
        }else {
            leave.setStatus(Const.BusinessStatus.CANCELED.getCode());
        }
        leaveMapper.updateByPrimaryKeySelective(leave);
        runtimeService.setVariable(processInstanceId,"enrty",leave);
    }

}
