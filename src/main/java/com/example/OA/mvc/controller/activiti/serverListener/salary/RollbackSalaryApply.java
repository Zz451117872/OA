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
 * 回滚薪资调整
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
		String processInstanceId = execution.getProcessInstanceId();
		ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		String businessKey = instance.getBusinessKey();
		SalaryAdjust salaryAdjust =  salaryAdjustMapper.selectByPrimaryKey(Integer.parseInt(businessKey));

		User user = userMapper.selectByPrimaryKey(salaryAdjust.getApplication());
		BigDecimal baseMoney = (BigDecimal) execution.getVariable("baseMoney");
		user.setSalary(baseMoney);
		user.setUpdateTime(new Date());
		userMapper.updateByPrimaryKeySelective(user);
	}


}
