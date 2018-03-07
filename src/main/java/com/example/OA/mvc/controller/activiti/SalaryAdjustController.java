package com.example.OA.mvc.controller.activiti;

import com.example.OA.dao.activiti.SalaryAdjustMapper;
import com.example.OA.model.User;
import com.example.OA.model.activiti.SalaryAdjust;
import com.example.OA.model.VO.SalaryAdjustVO;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.activiti.WorkflowService;
import com.google.common.collect.Maps;
import org.activiti.engine.RuntimeService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by aa on 2017/11/10.
 * 薪水调整流程控制器
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

    //开启薪资调整流程
    @RequestMapping(value = "start_salary_workflow.do",method = RequestMethod.POST)
    public ServerResponse startWorkflow(@Valid SalaryAdjust salaryAdjust , BindingResult bindingResult) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(bindingResult.hasErrors())
        {
            throw new AppException(Error.PARAMS_ERROR,bindingResult.getFieldError().getDefaultMessage());
        }
        User user = getUserBySubject(subject);
        if(salaryAdjust != null)
        {
            //SalaryAdjust相关信息
            salaryAdjust.setApplication(user.getId());//设置申请人
            salaryAdjust.setCreateTime(new Date());     //申请时间
            salaryAdjust.setStatus(Const.BusinessStatus.APPLICATION.getCode());//业务状态

            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("baseMoney", user.getSalary());  //原有薪金(回滚用)
            variables.put("inputUser",user.getUsername());   //设置申请人
            return workflowService.startSalaryAdjustWorkflow(salaryAdjust, variables);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //获取 薪资调整详细
    @RequestMapping(value = "get_salary_detail.do",method = RequestMethod.POST)
    public SalaryAdjustVO getSalaryAdjust(@RequestParam(value = "salaryId",required = true) Integer salaryId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return workflowService.getSalaryDetail(salaryId);
    }


    //修改申请 ，重新申请
    @RequestMapping(value = "modify_salary.do",method = RequestMethod.POST)
    public void modifySalaryAdjust(@Valid SalaryAdjust salaryAdjust,BindingResult bindingResult,
                                   @RequestParam(value = "reApply",required = true) Boolean reApply,
                                   @RequestParam(value = "taskId",required = true) String taskId,
                                   @RequestParam(value = "comment",required = false,defaultValue = "我无语") String comment) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(bindingResult.hasErrors())
        {
            throw new AppException(Error.PARAMS_ERROR,bindingResult.getFieldError().getDefaultMessage());
        }
        User user = getUserBySubject(subject);
        Map<String,Object> variables = Maps.newHashMap();
        variables.put("reApply",reApply);

        if(reApply)
        {   //可以修改属性：薪资调整金额，描述
            variables.put("adjustMoney",salaryAdjust.getAdjustmoney());
            variables.put("description",salaryAdjust.getDescription());
        }
        workflowService.completeTask(user,taskId,variables,comment);
        return;
    }
}
