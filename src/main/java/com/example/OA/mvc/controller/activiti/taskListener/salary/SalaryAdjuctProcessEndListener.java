package com.example.OA.mvc.controller.activiti.taskListener.salary;

import com.example.OA.dao.activiti.SalaryAdjustMapper;
import com.example.OA.model.activiti.SalaryAdjust;
import com.example.OA.mvc.common.Const;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 薪水调整流程结束监听器，部署在工作流程中，在流程“end”时调用，还有“start,take”等
 *  ，主要用于对业务对象的数据进行更新
 *
 */
@Service
@Transactional
public class SalaryAdjuctProcessEndListener implements ExecutionListener {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    SalaryAdjustMapper salaryAdjustMapper;


    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String businessKey = instance.getBusinessKey();

        SalaryAdjust salaryAdjust = salaryAdjustMapper.selectByPrimaryKey(Integer.parseInt(businessKey));
       Object result = execution.getVariable("result");//这个参数是以“expression”形式设置在连线的监听器上
      if("ok".equals(result))
      {
          salaryAdjust.setStatus(Const.BusinessStatus.PASSED.getCode());
          logger.info("薪资调整流程通过+++++++++++++++++++++++++++++++++");
      }else if("reject".equals(result))
      {
          salaryAdjust.setStatus(Const.BusinessStatus.REJECTED.getCode());
          logger.info("薪资调整流程被拒绝+++++++++++++++++++++++++++++++++");
      }else if("cancled".equals(result))
      {
          salaryAdjust.setStatus(Const.BusinessStatus.CANCELED.getCode());
          logger.info("薪资调整流程已取消+++++++++++++++++++++++++++++++++");
      }else {
          logger.info("薪资调整流程异常+++++++++++++++++++++++++++++++++");
      }
        salaryAdjustMapper.updateByPrimaryKeySelective(salaryAdjust);
    }
}
