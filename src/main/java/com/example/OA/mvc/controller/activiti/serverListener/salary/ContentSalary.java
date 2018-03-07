package com.example.OA.mvc.controller.activiti.serverListener.salary;

import java.math.BigDecimal;
import java.util.Date;

import com.example.OA.dao.UserMapper;
import com.example.OA.dao.activiti.SalaryAdjustMapper;
import com.example.OA.model.User;
import com.example.OA.model.activiti.SalaryAdjust;
import com.example.OA.util.BigDecimalUtil;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
薪水调整 服务监听器，部署在流程中，工作流通过时调用，用于对薪水的调整
 */
@Component
public class ContentSalary implements JavaDelegate {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private SalaryAdjustMapper salaryAdjustMapper;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		//获取业务id，并获取业务对象
		Integer businessKey = null;
		try{
			businessKey = (Integer) execution.getVariable("businessKey");
		}catch (Exception e)
		{
			businessKey = Integer.parseInt((String) execution.getVariable("businessKey"));
		}
		SalaryAdjust salaryAdjust = salaryAdjustMapper.selectByPrimaryKey(businessKey);
		//获取申请人数据
		User user = userMapper.selectByPrimaryKey(salaryAdjust.getApplication());
		BigDecimal old = user.getSalary();
		//对申请人的薪水进行调整
		user.setSalary(BigDecimalUtil.add(user.getSalary().doubleValue(),salaryAdjust.getAdjustmoney().doubleValue()));
		user.setUpdateTime(new Date());

		userMapper.updateByPrimaryKeySelective(user);
		logger.info(user.getUsername()+" 更新了工资，从 "+old+" 到 "+ user.getSalary());
	}

}
