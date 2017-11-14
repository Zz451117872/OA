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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public String startLeaveWorkflow(Leave leave, Map<String, Object> variables) {
        try {
            leaveMapper.insertSelective(leave); //存储业务对象，自动返回主键
            logger.info("save leave: {}", leave);   //保存请假单
            identityService.setAuthenticatedUserId(leave.getApplication().toString());

            String businessKey = leave.getId().toString();  //流程实例  与  请假单的一种对应关系
            leave.setBusinesskey(businessKey);
            variables.put("entry",leave);

            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(Const.processDefinitionKey.LEAVE, businessKey, variables);
            String processInstanceId = processInstance.getId();

            leave.setProcessinstanceid(processInstanceId);  //流程实例id 放入 请假单
            leave.setBusinesskey(businessKey);
            leaveMapper.updateByPrimaryKeySelective(leave);

            runtimeService.setVariable(processInstanceId,"entry",leave); //业务对象更新后，同步更新下变量
            return processInstanceId;
        }catch (Exception e) {
            throw new AppException(Error.WORKFLOW_INNER_ERROR,e.getMessage());
        }finally
        {
            identityService.setAuthenticatedUserId(null);
        }
    }

    // 开启薪资调整流程
    @Transactional
    public String startSalaryAdjustWorkflow(SalaryAdjust salaryAdjust, Map<String, Object> variables) {
        // 保存 薪资调整 对象
        try {
            salaryAdjustMapper.insertSelective(salaryAdjust);   //存储业务对象，自动 返回主键
            logger.info("save salaryAdjust: {}", salaryAdjust);   //保存请假单

            identityService.setAuthenticatedUserId(salaryAdjust.getApplication().toString());//这个听说要这样写。。。
            variables.put("entry",salaryAdjust);
            variables.put("businessKey", salaryAdjust.getId()); //这个变量在薪资统计时可以获取对应的用户

            String businessKey = salaryAdjust.getId()+"";  //流程实例  与  业务的一种对应关系
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(Const.processDefinitionKey.SALARY, businessKey, variables);
            String processInstanceId = processInstance.getId();

            salaryAdjust.setProcessinstanceid(processInstanceId);  //流程实例id 放入 请假单
            salaryAdjust.setBusinesskey(businessKey);
            salaryAdjustMapper.updateByPrimaryKeySelective(salaryAdjust);

            runtimeService.setVariable(processInstanceId,"entry",salaryAdjust);

            return processInstanceId;
        }catch (Exception e) {
            throw new AppException(Error.WORKFLOW_INNER_ERROR,e.getMessage());
        }finally
        {
            identityService.setAuthenticatedUserId(null);
        }
    }

    //委托任务
    public void doDelegateTask(String fromUsername,Integer ToUserId, String taskId) {
        try{
            Task task = taskService.createTaskQuery().taskAssignee(fromUsername).taskId(taskId).singleResult();
            if(task != null)
            {
                User user = userMapper.selectByPrimaryKey(ToUserId);
                if(user != null)
                {
                    taskService.delegateTask(taskId,user.getUsername());
                }
                throw new AppException(Error.DATA_VERIFY_ERROR,"被委托人不存在");
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"委托人没有该任务");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            throw new AppException(Error.WORKFLOW_INNER_ERROR);
        }
    }

    //转办任务
    @Transactional
    public void doTransferTask(String fromUsername,Integer toUserId, String taskId){
        try{
            Task task = taskService.createTaskQuery().taskAssignee(fromUsername).taskId(taskId).singleResult();
            if(task != null)
            {
                User toUser = userMapper.selectByPrimaryKey(toUserId);
                if(toUser != null)
                {
                    String assign = task.getAssignee();
                    this.taskService.setAssignee(taskId, toUser.getUsername());
                    this.taskService.setOwner(taskId, assign);
                }
                throw new AppException(Error.DATA_VERIFY_ERROR,"被转办人不存在");
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"转办人没有该任务");
        }catch (AppException e1)
        {
            throw e1;
        }catch (Exception e2)
        {
            throw new AppException(Error.WORKFLOW_INNER_ERROR);
        }
    }

    //完成任务
    @Transactional
    public void completeTask(User user, String taskId, Map<String,Object> var , String comment) {
        try{
            Task task = taskService.createTaskQuery().taskAssignee(user.getUsername()).taskId(taskId).singleResult();
            if(task != null)
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
                        return;
                    }
                    //完成正常任务
                    taskService.complete(taskId, var);
                    return ;
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"用户没有该任务");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            throw new AppException(Error.WORKFLOW_INNER_ERROR);
        }
    }

    //认领任务
    @Transactional
    public void claim(String username, String taskId) {
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
                throw new AppException(Error.DATA_VERIFY_ERROR,"未找到任务1");
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"未找到任务2");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            throw new AppException(Error.WORKFLOW_INNER_ERROR);
        }
    }

    // 获取个人任务列表
    @Transactional
    public List<BaseVO> findTodoTasks(String username) {
        try{
            List<Task> tasks = taskService.createTaskQuery()//
                    .taskCandidateOrAssigned(username)//
                    .orderByProcessDefinitionId()//
                    .orderByTaskCreateTime().desc().list();

            List<BaseVO> taskList = getBaseVOList(tasks);
            return taskList;
        }catch (Exception e)
        {
            throw e;
        }
    }

    //查询正在运行的 薪资调整 流程
    @Transactional
    public List<BaseVO> listRuningSalaryAdjust()  {
        try{
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
        }catch (Exception e)
        {
            throw e;
        }
    }

    //查询正在运行的 请假 流程
    public List<BaseVO> listRuningLeave()  {
        try{
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
        }catch (Exception e)
        {
            throw e;
        }

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
    @Transactional
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
    @Transactional
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
    @Transactional
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
            throw new AppException(Error.TARGET_NO_EXISTS,"任务不存在");
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
        throw new AppException(Error.TARGET_NO_EXISTS,"对应的流程实例不存在");
    }
}
