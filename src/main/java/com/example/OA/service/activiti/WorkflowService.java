package com.example.OA.service.activiti;

import com.example.OA.dao.activiti.LeaveMapper;
import com.example.OA.dao.UserMapper;
import com.example.OA.dao.activiti.SalaryAdjustMapper;
import com.example.OA.model.User;
import com.example.OA.model.activiti.*;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.CommonService;
import com.example.OA.util.BeanUtils;
import com.example.OA.util.BigDecimalUtil;
import com.google.common.collect.Lists;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by aa on 2017/11/7.
 */
@Service
public class WorkflowService extends CommonService{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    HistoryService historyService;

    @Autowired
    TaskService taskService;

    @Autowired
    LeaveMapper leaveMapper;

    @Autowired
    IdentityService identityService;

    @Autowired
    SalaryAdjustMapper salaryAdjustMapper;

    @Autowired
    UserMapper userMapper;

    // 开启请假流程
    public String startLeaveWorkflow(Leave leave, Map<String, Object> variables) {
        leaveMapper.insertSelective(leave);
        logger.info("save leave: {}", leave);   //保存请假单

        identityService.setAuthenticatedUserId(leave.getApplication().toString());


        String businessKey = leave.getId().toString();  //流程实例  与  请假单的一种对应关系
        leave.setBusinesskey(businessKey);
        variables.put("entry",leave);

        ProcessInstance processInstance = null;
        try {
            processInstance = runtimeService.startProcessInstanceByKey(Const.processDefinitionKey.LEAVE, businessKey, variables);
            String processInstanceId = processInstance.getId();

            leave.setProcessinstanceid(processInstanceId);  //流程实例id 放入 请假单
            leave.setBusinesskey(businessKey);
            leaveMapper.updateByPrimaryKeySelective(leave);

            return processInstanceId;
        }catch (Exception e) {
            throw e;
        }finally
        {
            identityService.setAuthenticatedUserId(null);
        }
    }

    // 开启薪资调整流程
    public String startSalaryAdjustWorkflow(SalaryAdjust salaryAdjust, Map<String, Object> variables) {
        // 保存 薪资调整 对象
        salaryAdjustMapper.insertSelective(salaryAdjust);
        logger.info("save salaryAdjust: {}", salaryAdjust);   //保存请假单


        identityService.setAuthenticatedUserId(salaryAdjust.getApplication().toString());//这个听说要这样写。。。
        variables.put("entry",salaryAdjust);
        variables.put("businessKey", salaryAdjust.getId());

        String businessKey = salaryAdjust.getId()+"";  //流程实例  与  业务的一种对应关系
        ProcessInstance processInstance = null;
        try {
            processInstance = runtimeService.startProcessInstanceByKey(Const.processDefinitionKey.SALARY, businessKey, variables);
            String processInstanceId = processInstance.getId();

            salaryAdjust.setProcessinstanceid(processInstanceId);  //流程实例id 放入 请假单
            salaryAdjust.setBusinesskey(businessKey);
            salaryAdjustMapper.updateByPrimaryKeySelective(salaryAdjust);

            runtimeService.setVariable(processInstanceId,"entry",salaryAdjust);

            return processInstanceId;
        }catch (Exception e) {
            throw e;
        }finally
        {
            identityService.setAuthenticatedUserId(null);
        }
    }

    //委托任务
    public void doDelegateTask(Integer fromUserId,Integer ToUserId, String taskId) {
        try{
            User user = userMapper.selectByPrimaryKey(ToUserId);
            taskService.delegateTask(taskId,user.getUsername());
        }catch (Exception e)
        {
            throw e;
        }
    }

    //转办任务
    public void doTransferTask(Integer fromUserId,Integer toUserId, String taskId){
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        User toUser = userMapper.selectByPrimaryKey(toUserId);
        if(task != null){
            String assign = task.getAssignee();
            this.taskService.setAssignee(taskId, toUser.getUsername());
            this.taskService.setOwner(taskId, assign);
        }else{
            throw new AppException(Error.NO_EXISTS,"要转办的任务 不存在 ");
        }
    }

    //完成任务
    public void completeTask(User user, String taskId, Map<String,Object> var , String comment) {
        try{
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if(task != null)
            {
                String assignee = task.getAssignee();
                if(assignee == null || "".equals(assignee))
                    throw new AppException(Error.NO_EXISTS,"任务未分配");
                if(assignee.equals(user.getUsername())) //如果任务的办理人 与 当前用户一至，就办理任务
                {
                    ProcessInstance instance = runtimeService//
                            .createProcessInstanceQuery()//
                            .processInstanceId(task.getProcessInstanceId())//
                            .singleResult();

                    identityService.setAuthenticatedUserId(user.getId().toString());
                    if(comment != null){
                        this.taskService.addComment(taskId, instance.getId(), comment);
                    }

                    // 完成委派任务
                    if(DelegationState.PENDING == task.getDelegationState()){
                        this.taskService.resolveTask(taskId, var);
                        logger.info("getDelegationState:"+(DelegationState.PENDING == task.getDelegationState()));
                        return;
                    }
                    //完成正常任务
                    taskService.complete(taskId, var);
                    return ;
                }
                throw new AppException(Error.UN_AUTHORIZATION,"指定任务办理人错误");
            }
            throw new AppException(Error.UN_AUTHORIZATION,"没有找到指定任务");
        }catch (Exception e)
        {
            throw e;
        }
    }

    //认领任务
    public void claim(String username, String taskId) {
        if(username == null || taskId == null)
            throw new AppException(Error.PARAMS_ERROR);
        try{
            List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
            if(tasks != null && !tasks.isEmpty())
            {
                for(Task task : tasks)
                {
                    if(task.getId().equals(taskId))
                    {
                        taskService.claim(taskId, username);
                        return;
                    }
                }
                throw new AppException(Error.NO_EXISTS,"没有该任务");
            }
            throw new AppException(Error.NO_EXISTS,"没有任务可供领取");
        }catch (Exception e)
        {
            throw e;
        }
    }

    // 获取个人任务列表
    public List<BaseVO> findTodoTasks(String username) {
        if(username == null)
            throw new AppException(Error.PARAMS_ERROR);
        try{
            TaskQuery taskQuery = this.taskService.createTaskQuery().taskCandidateOrAssigned(username);
            List<Task> tasks = taskQuery.orderByProcessDefinitionId().orderByTaskCreateTime().desc().list();
            List<BaseVO> taskList = getBaseVOList(tasks);
            return taskList;
        }catch (Exception e)
        {
            throw e;
        }
    }

    //查询正在运行的 薪资调整 流程
    public List<BaseVO> listRuningSalaryAdjust()  {
        List<SalaryAdjust> salaryAdjustList = salaryAdjustMapper.getByApplicationAndStatus(null,Const.WorkflowStatus.APPLICATION.getCode()) ;
        List<BaseVO> result = Lists.newArrayList();

        if(salaryAdjustList != null ){
            for(SalaryAdjust salaryAdjust : salaryAdjustList){
                if(salaryAdjust.getProcessinstanceid() == null){
                    continue;
                }
                // 查询流程实例
                String processInstanceId = salaryAdjust.getProcessinstanceid();
                result.add(getBaseVOByProcessInstance(processInstanceId));
            }
        }
        return result;
    }

    //查询正在运行的 请假 流程
    public List<BaseVO> listRuningLeave()  {
        List<Leave> leaves = leaveMapper.getByApplicationAndStatus(null,Const.WorkflowStatus.APPLICATION.getCode()) ;
        List<BaseVO> result = Lists.newArrayList();

        if(leaves != null ){
            for(Leave leave : leaves){
                if(leave.getProcessinstanceid() == null){
                    continue;
                }
                // 查询流程实例
                String processInstanceId = leave.getProcessinstanceid();
                result.add(getBaseVOByProcessInstance(processInstanceId));
            }
        }
        return result;
    }

    //获取所有运行中的流程实例
    public List<BaseVO> findRunningProcessInstaces() {
       try{
           List<BaseVO> result = Lists.newArrayList();
           List<ProcessInstance> processInstances = runtimeService//
                   .createProcessInstanceQuery().active()//
                   .orderByProcessDefinitionId().desc().list();

           for(ProcessInstance instance : processInstances)
           {
               BaseVO baseVO = (BaseVO) runtimeService.getVariable(instance.getId(),"entry");
               result.add(baseVO);
           }
           return result;
       }catch (Exception e)
       {
           throw e;
       }
    }

    //获取所有已结束的流程实例
    public List<BaseVO> findFinishedProcessInstaces() {
        try {
            List<HistoricProcessInstance> historicProcessInstances = historyService
                        .createHistoricProcessInstanceQuery()//
                        .finished().orderByProcessDefinitionId().desc().list();

            List<BaseVO> result = Lists.newArrayList();
            for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
                String processInstanceId = historicProcessInstance.getId();

                List<HistoricVariableInstance> listVar = this.historyService//
                        .createHistoricVariableInstanceQuery()//
                        .processInstanceId(processInstanceId).list();

                for (HistoricVariableInstance var : listVar) {
                    if ("serializable".equals(var.getVariableTypeName()) && "entry".equals(var.getVariableName())) {
                        BaseVO base = (BaseVO) var.getValue();
                        base.setHistoricProcessInstance(historicProcessInstance);
                        base.setProcessDefinition(getProcessDefinition(historicProcessInstance.getProcessDefinitionId()));
                        result.add(base);
                        break;
                    }
                }
            }
            return result;
        }catch (Exception e)
        {
            throw e;
        }
    }

    //查看个人历史完成任务
    public List<BaseVO> findFinishedTaskInstances(String username){
        try {
            List<HistoricTaskInstance> list = historyService//
                    .createHistoricTaskInstanceQuery()//
                    .taskAssignee(username).finished()//
                    .orderByHistoricTaskInstanceEndTime().desc().list();
            List<BaseVO> result = Lists.newArrayList();

            for (HistoricTaskInstance historicTaskInstance : list) {
                String processInstanceId = historicTaskInstance.getProcessInstanceId();

                List<HistoricVariableInstance> listVar = historyService//
                        .createHistoricVariableInstanceQuery()//
                        .processInstanceId(processInstanceId).list();

                for (HistoricVariableInstance var : listVar) {
                    if ("serializable".equals(var.getVariableTypeName()) && "entry".equals(var.getVariableName())) {
                        BaseVO base = (BaseVO) var.getValue();
                        base.setHistoricTaskInstance(historicTaskInstance);
                        base.setProcessDefinition(getProcessDefinition(historicTaskInstance.getProcessDefinitionId()));
                        result.add(base);
                        break;
                    }
                }
            }
            return result;
        }catch (Exception e){
            throw e;
        }
    }

    //获取评论信息
    public List<CommentVO> getComments(String taskId){
        try{
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if(task != null) {
                List<Comment> comments = this.taskService.getProcessInstanceComments(task.getProcessInstanceId());
                List<CommentVO> commnetList = Lists.newArrayList();
                for (Comment comment : comments) {
                    User user = userMapper.selectByPrimaryKey(Integer.parseInt(comment.getUserId()));
                    CommentVO vo = new CommentVO();
                    vo.setContent(comment.getFullMessage());
                    vo.setTime(comment.getTime());
                    vo.setUserName(user.getUsername());
                    commnetList.add(vo);
                }
                return commnetList;
            }
            throw new AppException(Error.NO_EXISTS,"任务不存在");
        }catch (Exception e)
        {
            throw e;
        }
    }

    //薪资调整
    public void contentSalary(Execution exe){
        SalaryAdjust salaryAdjust = (SalaryAdjust) this.runtimeService.getVariable(exe.getProcessInstanceId(), "entity");
        User user = userMapper.selectByPrimaryKey(salaryAdjust.getApplication());
        user.setSalary(BigDecimalUtil.add(user.getSalary().doubleValue(),salaryAdjust.getAdjustmoney().doubleValue()));
        user.setUpdateTime(new Date());
        userMapper.updateByPrimaryKeySelective(user);
    }

    //回滚薪资调整
    public void rollbackApply(Execution exe){
        SalaryAdjust salaryAdjust = (SalaryAdjust)this.runtimeService.getVariable(exe.getProcessInstanceId(), "entity");
        BigDecimal baseMoney = (BigDecimal) this.runtimeService.getVariable(exe.getProcessInstanceId(), "baseMoney");
        User user = userMapper.selectByPrimaryKey(salaryAdjust.getApplication());
        user.setSalary(baseMoney);
        user.setUpdateTime(new Date());
        userMapper.updateByPrimaryKeySelective(user);
    }

    private ProcessDefinition getProcessDefinition(String processDefinitionId) {
        return repositoryService.createProcessDefinitionQuery()//
                .processDefinitionId(processDefinitionId).singleResult();
    }

    private List<BaseVO> getBaseVOList(List<Task> tasks) {
        List<BaseVO> taskList = Lists.newArrayList();
        for (Task task : tasks) {
            String processInstanceId = task.getProcessInstanceId();
            ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).active().singleResult();
            if(BeanUtils.isBlank(processInstance)){
                //如果有挂起的流程则continue
                continue;
            }
            //获取当前流程下的key为entity的variable
            BaseVO base = (BaseVO) this.runtimeService.getVariable(processInstance.getId(), "entry");
            base.setTaskBean(convertTask(task));
            base.setProcessInstance(processInstance);
            base.setProcessDefinition(getProcessDefinition(processInstance.getProcessDefinitionId()));
            taskList.add(base);
        }
        return taskList;
    }

    private BaseVO getBaseVOByProcessInstance(String processInstanceId) {

        ProcessInstance instance = this.runtimeService//
                .createProcessInstanceQuery()//
                .processInstanceId(processInstanceId).singleResult();

        Task task = this.taskService.createTaskQuery()//
                .processInstanceId(processInstanceId).singleResult();
        if (instance != null) {
            BaseVO base = (BaseVO) this.runtimeService.getVariable(instance.getId(), "entry");
            base.setTaskBean(convertTask(task));
            base.setProcessInstance(instance);
            base.setProcessDefinition(getProcessDefinition(instance.getProcessDefinitionId()));
            return base;
        }
        throw new AppException(Error.NO_EXISTS,"对应的流程实例不存在");
    }
}
