package com.example.OA.mvc.controller.activiti;

import com.example.OA.dao.RoleMapper;
import com.example.OA.dao.UserRoleMapper;
import com.example.OA.dao.activiti.LeaveMapper;
import com.example.OA.model.Role;
import com.example.OA.model.activiti.Leave;
import com.example.OA.model.User;
import com.example.OA.model.VO.LeaveVO;
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
import java.util.*;

/**
 * Created by aa on 2017/11/3.
 */
@RestController
@RequestMapping("leave")
public class LeaveController extends CommonController{

    @Autowired
    LeaveMapper leaveMapper;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    UserRoleMapper userRoleMapper;

    @Autowired
    RoleMapper roleMapper;

    //启动请假流程   要进行表单验证
    @RequestMapping(value = "start_leave_workflow.do",method = RequestMethod.POST)
    public ServerResponse startWorkflow(@Valid Leave leave , BindingResult bindingResult) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(bindingResult.hasErrors())
        {
            throw new AppException(Error.PARAMS_ERROR,bindingResult.getFieldError().getDefaultMessage());
        }
        try{
            if(leave != null)
            {
                User user = getUserBySubject(subject);
                //leave 相关相关属性
                leave.setApplication(user.getId()); //设置申请人
                leave.setCreateTime(new Date());
                leave.setStatus(Const.BusinessStatus.APPLICATION.getCode());

                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("inputUser",user.getUsername());//设置申请人变量
                List<Integer> roleIds = userRoleMapper.getRoleidByUserid(user.getId());
                if(roleIds != null)
                {       //目前一个用户只有一个角色，后期优化
                    Role role = roleMapper.selectByPrimaryKey(roleIds.get(0));
                    variables.put("role",role.getRoleName());
                }else{
                    variables.put("role","employee");
                }

                return workflowService.startLeaveWorkflow(leave, variables);
                }
                throw new AppException(Error.PARAMS_ERROR);
            }catch (Exception e)
            {
                throw e;
            }
    }


    //获取 请假单详细
    @RequestMapping(value = "get_leave_detail.do",method = RequestMethod.POST)
    public LeaveVO getLeaveDetail(@RequestParam(value = "leaveId",required = true) Integer leaveId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject .isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return workflowService.getLeaveDetail(leaveId);
    }


    //请假申请修改，重新申请
    @RequestMapping(value = "modify_leave.do",method = RequestMethod.POST)
    public void modifyLeave(@Valid Leave leave, BindingResult bindingResult,
                            @RequestParam(value = "reApply",required = true) Boolean reApply,
                            @RequestParam(value = "taskId",required = true) String taskId,
                            @RequestParam(value = "comment",required = false,defaultValue = "我无语")String comment) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(bindingResult.hasErrors())
        {
            throw new AppException(Error.PARAMS_ERROR,bindingResult.getFieldError().getDefaultMessage());
        }
        User user = getUserBySubject(subject);
        try{
            if(leave != null) {
                Map<String, Object> variables = Maps.newHashMap();
                variables.put("reApply", reApply);
                if (reApply) {   //可以修改的内容：请假天数，原因，开始结束时间，请假类型
                    variables.put("leaveNumber", leave.getLeaveNumber());
                    variables.put("reason", leave.getReason());
                    variables.put("startTime", leave.getStartTime());
                    variables.put("endTime", leave.getEndTime());
                    variables.put("leaveType", leave.getLeaveType());
                }
                workflowService.completeTask(user, taskId, variables, comment);
                return;
            }
            throw new AppException(Error.PARAMS_ERROR);
        }catch (Exception e)
        {
            throw e;
        }
    }


}
