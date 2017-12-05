package com.example.OA.mvc.controller.activiti.taskListener.salary;

import com.example.OA.dao.activiti.SalaryAdjustMapper;
import com.example.OA.model.activiti.SalaryAdjust;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class SalaryModifyApplyProcessor implements TaskListener {

	@Autowired
	SalaryAdjustMapper salaryAdjustMapper;

	@Autowired
	RuntimeService runtimeService;

	private  Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void notify(DelegateTask delegateTask){
		try {
			boolean reApply =(Boolean) delegateTask.getVariable("reApply");
			ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(delegateTask.getProcessInstanceId()).singleResult();
			SalaryAdjust salaryAdjust = salaryAdjustMapper.selectByPrimaryKey(Integer.parseInt(instance.getBusinessKey()));
			if(reApply)
			{
				salaryAdjust.setAdjustmoney((BigDecimal) delegateTask.getVariable("adjustMoney"));
				salaryAdjust.setDescription((String) delegateTask.getVariable("description"));
			}
			salaryAdjustMapper.updateByPrimaryKeySelective(salaryAdjust);
			runtimeService.setVariable(salaryAdjust.getProcessinstanceid(),"enrty",salaryAdjust);
			logger.info("薪资修改完成！");
		} catch (Exception e) {
			logger.error("薪资修改失败！");
			e.printStackTrace();
		}
	}

}
