package com.example.OA.mvc.controller.activiti.serverListener.salary;

import java.math.BigDecimal;
import java.util.Date;

import com.example.OA.dao.UserMapper;
import com.example.OA.dao.activiti.SalaryAdjustMapper;
import com.example.OA.model.User;
import com.example.OA.model.activiti.SalaryAdjust;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 回滚薪资调整 ，在工作流经过时调用，用于对薪水的回滚
 */

@Component
public class RollbackSalaryApply implements JavaDelegate {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	RuntimeService runtimeService;

	@Autowired
	SalaryAdjustMapper salaryAdjustMapper;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		//获取流程实例
		String processInstanceId = execution.getProcessInstanceId();
		ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		String businessKey = instance.getBusinessKey();
		//获取业务对象
		SalaryAdjust salaryAdjust =  salaryAdjustMapper.selectByPrimaryKey(Integer.parseInt(businessKey));
		//获取申请人数据
		User user = userMapper.selectByPrimaryKey(salaryAdjust.getApplication());
		//获取调整前工资
		BigDecimal baseMoney = (BigDecimal) execution.getVariable("baseMoney");
		//回滚工资到调整前工资
		user.setSalary(baseMoney);
		user.setUpdateTime(new Date());
		userMapper.updateByPrimaryKeySelective(user);
	}


}
