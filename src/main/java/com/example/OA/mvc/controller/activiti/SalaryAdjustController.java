package com.example.OA.mvc.controller.activiti;

import com.example.OA.dao.activiti.SalaryAdjustMapper;
import com.example.OA.model.User;
import com.example.OA.model.activiti.SalaryAdjust;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.activiti.WorkflowService;
import com.example.OA.util.BeanUtils;
import com.google.common.collect.Maps;
import org.activiti.engine.RuntimeService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by aa on 2017/11/10.
 */
@RestController
@RequestMapping("salary")
public class SalaryAdjustController extends CommonController{

    @Autowired
    SalaryAdjustMapper salaryAdjustMapper;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    RuntimeService runtimeService;

    //开启薪资调整流程   要进行表单验证
    @RequestMapping(value = "start_salary_workflow",method = RequestMethod.POST)
    public String startWorkflow(SalaryAdjust salaryAdjust) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        if(salaryAdjust != null)
        {
            try{

                   //BaseVO 相关信息
                   salaryAdjust.setApplication(user.getId());
                   salaryAdjust.setApplicationName(user.getUsername());
                   salaryAdjust.setBusinesstype(Const.BusinessType.SALARY);

                   //SalaryAdjust相关信息
                   salaryAdjust.setCreateTime(new Date());
                   salaryAdjust.setStatus(Const.WorkflowStatus.APPLICATION.getCode());

                   Map<String, Object> variables = new HashMap<String, Object>();
                   variables.put("baseMoney", user.getSalary());  //原有薪金(回滚用)
                   variables.put("inputUser",user.getUsername());   //设置申请人
                   return workflowService.startSalaryAdjustWorkflow(salaryAdjust, variables);

            }catch (Exception e)
            {
                throw e;
            }
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "salary_by_status",method = RequestMethod.POST)
    public List<SalaryAdjust> getSalaryAddustByStatus(Integer status) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        try{
            Const.WorkflowStatus.codeof(status);   // 检查传入状态参数
        }catch (Exception e)
        {
            throw new AppException(Error.PARAMS_ERROR,"status 参数错误");
        }
        User user = getUserBySubject(subject);
        return salaryAdjustMapper.getByApplicationAndStatus(null,status);
    }

    //获取 薪资调整详细
    @RequestMapping(value = "salary_by_id",method = RequestMethod.POST)
    public SalaryAdjust getSalaryAdjust(Integer salaryId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(salaryId != null)
        {
            return salaryAdjustMapper.selectByPrimaryKey(salaryId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //办理任务
    @RequestMapping(value = "complete_salary",method = RequestMethod.POST)
    public void complete(String comment,String taskId,Boolean isPass) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        try{
            Map<String,Object> variables = Maps.newHashMap();
            variables.put("isPass",isPass);
            workflowService.completeTask(user,taskId,variables,comment);
            return;
        }catch (Exception e)
        {
            throw e;
        }
    }

    //修改申请
    @RequestMapping(value = "modify_salary",method = RequestMethod.POST)
    public void modifySalaryAdjust(SalaryAdjust salaryAdjust,Boolean reApply,String taskId,String comment) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        try{
            Map<String,Object> variables = Maps.newHashMap();
            variables.put("reApply",reApply);

            if(reApply)
            {
                variables.put("adjustMoney",salaryAdjust.getAdjustmoney());
                variables.put("description",salaryAdjust.getDescription());
            }
            workflowService.completeTask(user,taskId,variables,comment);
            return;
        }catch (Exception e)
        {
            throw e;
        }
    }
}
