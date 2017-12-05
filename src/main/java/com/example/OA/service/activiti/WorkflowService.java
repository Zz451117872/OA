package com.example.OA.service.activiti;

import com.example.OA.dao.activiti.LeaveMapper;
import com.example.OA.dao.UserMapper;
import com.example.OA.dao.activiti.SalaryAdjustMapper;
import com.example.OA.model.User;
import com.example.OA.model.VO.*;
import com.example.OA.model.activiti.*;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.common._PageInfo;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.CommonService;
import com.google.common.collect.Lists;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
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

//===============任务操作流程=======================
    // 开启请假流程
    @Transactional
    public ServerResponse startLeaveWorkflow(Leave leave, Map<String, Object> variables) {
        try {
            int result = leaveMapper.insertSelective(leave); //存储业务对象，自动返回主键
            if(result < 1)
            {
                throw new AppException(Error.DATABASE_OPERATION);
            }
            identityService.setAuthenticatedUserId(leave.getApplication().toString());

            variables.put("businessKey", leave.getId());
            String businessKey = leave.getId().toString();  //流程实例  与  请假单的一种对应关系

            variables.put("entry",leave);

            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(Const.processDefinitionKey.LEAVE, businessKey, variables);
            String processInstanceId = processInstance.getId();

            leave.setProcessinstanceid(processInstanceId);  //流程实例id 放入 请假单
            result = leaveMapper.updateByPrimaryKeySelective(leave);
            if(result < 1)
            {
                throw new AppException(Error.DATABASE_OPERATION);
            }
            runtimeService.setVariable(processInstanceId,"entry",leave);
            return ServerResponse.createBySuccess();
        }catch (AppException e) {
            throw e;
        }catch (Exception e)
        {
            logger.debug(e.getMessage());
            throw new AppException(Error.WORKFLOW_INNER_ERROR,e.getMessage());
        }finally {
            identityService.setAuthenticatedUserId(null);
        }
    }

    // 开启薪资调整流程
    @Transactional
    public ServerResponse startSalaryAdjustWorkflow(SalaryAdjust salaryAdjust, Map<String, Object> variables) {
        // 保存 薪资调整 对象
        try {
            int result = salaryAdjustMapper.insertSelective(salaryAdjust);   //存储业务对象，自动 返回主键
            if(result < 1)
            {
                throw new AppException(Error.DATABASE_OPERATION);
            }

            identityService.setAuthenticatedUserId(salaryAdjust.getApplication().toString());//这个听说要这样写。。。

            String businessKey = salaryAdjust.getId()+"";  //流程实例  与  业务的一种对应关系
            variables.put("businessKey", salaryAdjust.getId()); //这个变量在薪资统计时可以获取对应的用户
            variables.put("entry",salaryAdjust);

            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(Const.processDefinitionKey.SALARY, businessKey, variables);
            String processInstanceId = processInstance.getId();

            salaryAdjust.setProcessinstanceid(processInstanceId);  //流程实例id 放入 请假单
            result = salaryAdjustMapper.updateByPrimaryKeySelective(salaryAdjust);
            if(result < 1)
            {
                throw new AppException(Error.DATABASE_OPERATION);
            }
            runtimeService.setVariable(processInstanceId,"entry",salaryAdjust);
            return ServerResponse.createBySuccess();
        }catch (AppException e) {
            throw e;
        }catch (Exception e){
            logger.debug(e.getMessage());
            throw new AppException(Error.WORKFLOW_INNER_ERROR,e.getMessage());
        }finally {
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
                    return;
                }
                throw new AppException(Error.DATA_VERIFY_ERROR,"被委托人不存在");
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"委托人没有该任务");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            logger.debug(e.getMessage());
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
                    return;
                }
                throw new AppException(Error.DATA_VERIFY_ERROR,"被转办人不存在");
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"转办人没有该任务");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            logger.debug(e.getMessage());
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
                    taskService.setVariablesLocal(taskId,var);
                    taskService.complete(taskId, var);
                    return ;
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"用户没有该任务");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            logger.debug(e.getMessage());
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
            logger.debug(e.getMessage());
            throw new AppException(Error.WORKFLOW_INNER_ERROR);
        }
    }

///===========================================================
    //查找人个 或者 所有 任务
    public _PageInfo findTasks(String username,Integer pageNum,Integer pageSize) {
        try{
            List<Task> tasks = null;
            if(username == null)
            {
                tasks = taskService.createTaskQuery()//
                        .active().orderByProcessDefinitionId()//
                        .asc().orderByTaskCreateTime().desc().list();
            }else{
                tasks = taskService.createTaskQuery()//
                        .taskCandidateOrAssigned(username)//
                        .orderByProcessDefinitionId()//
                        .orderByTaskCreateTime().desc().list();
            }
            if(tasks != null)
            {
                List<BaseVO> baseVOs = getBaseVOList(tasks);
                return general_PageInfo(baseVOs,pageNum,pageSize);
            }
           return null;
        }catch (AppException e) {
            throw e;
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new AppException(Error.WORKFLOW_INNER_ERROR);
        }
    }

    /*
    通过 申请人 或者 业务状态 查找 个人 或者 所有 申请
    application：申请人
    status：业务状态，申请人和申请状态不可同时为null
     */
    public _PageInfo findApplications(Integer application, Integer status, Integer pageNum, Integer pageSize) {
        try {
            List<BaseVO> result = Lists.newArrayList();
            List<BaseVO> salaryAdjusts = findSalaryAdjusts(application,status);
            result.addAll(salaryAdjusts);
            List<BaseVO> leaves = findLeaves(application,status);
            result.addAll(leaves);

            return general_PageInfo(result,pageNum,pageSize);
        }catch (AppException e)
        {
           throw e;
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new AppException(Error.WORKFLOW_INNER_ERROR);
        }
    }

    //生成 分页信息
    private <T> _PageInfo  general_PageInfo(List<T> result, Integer pageNum, Integer pageSize) {

        _PageInfo pageInfo = new _PageInfo();
        if(result != null)
        {
            pageInfo.setTotal(result.size());
            pageInfo.setPages((int)Math.ceil((result.size()*1.0)/pageSize));
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(pageSize);
            int start = (pageNum - 1)*pageSize;
            if(start >= result.size())
            {
                pageInfo.setList(null);
            }else{
                if((start + pageSize ) <= result.size())
                {
                    pageInfo.setList(result.subList(start,start + pageSize));
                }else{
                    pageInfo.setList(result.subList(start,result.size()));
                }
            }
            return pageInfo;
        }
        return null;
    }

    //查看 个人 或者所有 历史完成任务
    public _PageInfo findHisrotyTasks(String username,Integer pageNum ,Integer pageSize){
        try {
            List<HistoricTaskInstance> list = null;
            if(username == null)
            {
                list = historyService//
                        .createHistoricTaskInstanceQuery()//
                        .finished().orderByProcessDefinitionId()//
                        .asc().orderByHistoricTaskInstanceEndTime().desc().list();
            }else {
                list = historyService//
                        .createHistoricTaskInstanceQuery()//
                        .taskAssignee(username).finished()//
                        .orderByHistoricTaskInstanceEndTime().desc().list();
            }
            List<HistoryTaskVO> result = Lists.newArrayList();
            for (HistoricTaskInstance historicTaskInstance : list) {
                String processInstanceId = historicTaskInstance.getProcessInstanceId();

                List<HistoricVariableInstance> listVar = historyService//
                        .createHistoricVariableInstanceQuery()//
                        .processInstanceId(processInstanceId).list();

                for (HistoricVariableInstance var : listVar) {
                    if ("serializable".equals(var.getVariableTypeName()) && "entry".equals(var.getVariableName())) {
                        Object obj =  var.getValue();
                        if(obj instanceof Leave)
                        {
                            result.add( convertHistoryTaskVOByLeave((Leave)obj,historicTaskInstance));
                            break;
                        }else if(obj instanceof SalaryAdjust)
                        {
                            result.add(convertHistoryTaskVOBySalary((SalaryAdjust)obj,historicTaskInstance));
                            break;
                        }
                        throw new AppException(Error.DATA_VERIFY_ERROR);
                    }
                }
            }
            return general_PageInfo(result,pageNum,pageSize);
        }catch (AppException e){
            throw e;
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new AppException(Error.WORKFLOW_INNER_ERROR);
        }
    }

    /*
    查找薪资调整流程，申请人 与 业务状态 不可同时为NULL
    application：申请人
    status：业务状态
     */
    public List<BaseVO> findSalaryAdjusts(Integer application,Integer status) {
        try {
            List<SalaryAdjust> salaryAdjustList = salaryAdjustMapper.getByApplicationOrStatus(application,status);
            List<BaseVO> result = Lists.newArrayList();
            if (salaryAdjustList != null && !salaryAdjustList.isEmpty()) {
                for (SalaryAdjust salaryAdjust : salaryAdjustList) {
                    result.add(convertBaseVOBySalary(salaryAdjust));
                }
            }
            return result;
        }catch (Exception e)
        {
            e.printStackTrace();
           throw e;
        }
    }

    /*
     查找请假流程，申请人 与 业务状态 不可同时为NULL
     application：申请人
     status：业务状态
      */
    public List<BaseVO> findLeaves(Integer application,Integer status)  {
        try{
            List<Leave> leaves = leaveMapper.getByApplicationOrStatus(application,status) ;
            List<BaseVO> result = Lists.newArrayList();

            if(leaves != null && !leaves.isEmpty()){
                for(Leave leave : leaves){
                    result.add(convertBaseVOByLeave(leave));
                }
            }
            return result;
        }catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    //获取请假流程的详细信息
    public LeaveVO getLeaveDetail(Integer leaveId) {
       try{
           Leave leave = leaveMapper.selectByPrimaryKey(leaveId);
           return convertLeaveVO(leave);
       }catch (Exception e)
       {
           e.printStackTrace();
           throw new AppException(Error.UNKNOW_EXCEPTION);
       }
    }

    //转化leave
    private LeaveVO convertLeaveVO(Leave leave) {
        try{
            if(leave != null)
            {
                User user = userMapper.selectByPrimaryKey(leave.getApplication());
                LeaveVO leaveVO = new LeaveVO(leave,user.getUsername());
                return leaveVO;
            }
            return null;
        }catch (Exception e)
        {
            throw e;
        }
    }

    //获取薪资调整流程的详细信息，包括评论
    public SalaryAdjustVO getSalaryDetail(Integer salaryId) {
        try{
            SalaryAdjust salary = salaryAdjustMapper.selectByPrimaryKey(salaryId);
            return convertSalaryVO(salary);
        }catch (Exception e)
        {
            logger.debug(e.getMessage());
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    //转化salary
    private SalaryAdjustVO convertSalaryVO(SalaryAdjust salary) {
        try{
            if(salary != null)
            {
                User user = userMapper.selectByPrimaryKey(salary.getApplication());
                SalaryAdjustVO salaryAdjustVO = new SalaryAdjustVO(salary,user.getUsername());
                return salaryAdjustVO;
            }
            return null;
        }catch (Exception e)
        {
            throw e;
        }
    }

    //获取评论信息
    public _PageInfo<CommentVO> findComments(String processInstanceId,Integer pageNum, Integer pageSize){
        try{
            if(processInstanceId != null) {
                List<Comment> comments = this.taskService.getProcessInstanceComments(processInstanceId);
                List<CommentVO> commnetList = Lists.newArrayList();
                for (Comment comment : comments) {
                    User user = userMapper.selectByPrimaryKey(Integer.parseInt(comment.getUserId()));
                    CommentVO vo = new CommentVO();
                    vo.setContent(comment.getFullMessage());
                    vo.setTime(comment.getTime());
                    vo.setUserName(user.getUsername());
                    commnetList.add(vo);
                }
                commnetList.sort(new Comparator<CommentVO>() {
                    @Override
                    public int compare(CommentVO o1, CommentVO o2) {
                        return o1.getTime().compareTo(o2.getTime());
                    }
                });
                return general_PageInfo(commnetList,pageNum,pageSize);
            }
            throw new AppException(Error.TARGET_NO_EXISTS,"流程不存在");
        }catch (Exception e)
        {
            throw e;
        }
    }

//=============工具方法===============================================

    //将历史任务实例 与 业务对象 salaryAdjust 转化为 HistoryTaskVO,返回前台
    private HistoryTaskVO convertHistoryTaskVOBySalary(SalaryAdjust salary, HistoricTaskInstance historicTaskInstance) {
        try{
            if(historicTaskInstance != null && salary != null)
            {
                HistoryTaskVO historyTaskVO = new HistoryTaskVO();
                String username = userMapper.getUsernameById(salary.getApplication());
                historyTaskVO.setApplicationName(username);
                historyTaskVO.setBusinesstype(Const.BusinessType.LEAVE);
                historyTaskVO.setStartTime(historicTaskInstance.getStartTime());
                historyTaskVO.setEndTime(historicTaskInstance.getEndTime());
                historyTaskVO.setApprover(historicTaskInstance.getAssignee());
                historyTaskVO.setApproveResult(getApproveResultByHistoryTaskInstance(historicTaskInstance));
                historyTaskVO.setClaimTime(historicTaskInstance.getClaimTime());
                historyTaskVO.setTaskId(historicTaskInstance.getId());
                historyTaskVO.setComment(getCommentByHistoryTaskInstance(historicTaskInstance));
                historyTaskVO.setBusinessInfo("调整工资："+salary.getAdjustmoney()+"元");
                return historyTaskVO;
            }
            return null;
        }catch (Exception e)
        {
            logger.debug(e.getMessage());
            throw e;
        }
    }

    //将历史任务实例 与 业务对象 leave 转化为 HistoryTaskVO,返回前台
    private HistoryTaskVO convertHistoryTaskVOByLeave(Leave leave, HistoricTaskInstance historicTaskInstance) {
        try{
            if(historicTaskInstance != null && leave != null)
            {
                HistoryTaskVO historyTaskVO = new HistoryTaskVO();
                String username = userMapper.getUsernameById(leave.getApplication());
                historyTaskVO.setApplicationName(username);
                historyTaskVO.setBusinesstype(Const.BusinessType.LEAVE);
                historyTaskVO.setStartTime(historicTaskInstance.getStartTime());
                historyTaskVO.setEndTime(historicTaskInstance.getEndTime());
                historyTaskVO.setApprover(historicTaskInstance.getAssignee());
                historyTaskVO.setApproveResult(getApproveResultByHistoryTaskInstance(historicTaskInstance));
                historyTaskVO.setClaimTime(historicTaskInstance.getClaimTime());
                historyTaskVO.setTaskId(historicTaskInstance.getId());
                historyTaskVO.setComment(getCommentByHistoryTaskInstance(historicTaskInstance));
                historyTaskVO.setBusinessInfo(leave.getLeaveType()+"&&"+leave.getLeaveNumber()+"天");
                return historyTaskVO;
            }
            return null;
        }catch (Exception e)
        {
            logger.debug(e.getMessage());
            throw e;
        }
    }

    //将 Leave 业务对象 转化为 BaseVO,返回前台
    private BaseVO convertBaseVOByLeave(Leave leave) {
        try{
            BaseVO baseVO = new BaseVO();
            String username = userMapper.getUsernameById(leave.getApplication());
            if(username == null)
            {
                throw new AppException(Error.DATA_VERIFY_ERROR,"申请人不存在");
            }
            baseVO.setApplication(leave.getApplication());
            baseVO.setBusinesstype(Const.BusinessType.LEAVE);
            baseVO.setBusinesskey(leave.getId().toString());
            baseVO.setApplicationName(username);
            baseVO.setStartTime(leave.getCreateTime());
            baseVO.setStatus(Const.BusinessStatus.codeof(leave.getStatus().intValue()).getValue());
            baseVO.setBusinessInfo("leave:" + leave.getLeaveType() + leave.getLeaveNumber() + "天");

            if(leave.getStatus() == Const.BusinessStatus.APPLICATION.getCode()) {
                ProcessInstance processInstance = getProcessInstanceById(leave.getProcessinstanceid());
                Task task = getTaskByProcessInstance(leave.getProcessinstanceid());
                if (processInstance != null && task != null) {
                    baseVO.setProcessInstanceId(processInstance.getId());
                    baseVO.setTaskId(task.getId());
                    baseVO.setApprover(task.getAssignee());
                    return baseVO;
                }else{
                    throw new AppException(Error.WORKFLOW_INNER_ERROR);
                }

            }else{
                HistoricProcessInstance   historicProcessInstance = getHistoryProcessInstanceById(leave.getProcessinstanceid());
                HistoricTaskInstance historicTaskInstance = getHistoricTaskInstance(leave.getProcessinstanceid());
                if (historicProcessInstance != null && historicTaskInstance != null) {
                    baseVO.setProcessInstanceId(historicProcessInstance.getId());
                    baseVO.setTaskId(historicTaskInstance.getId());
                    baseVO.setApprover(historicTaskInstance.getAssignee());
                    return baseVO;
                }else {
                    throw new AppException(Error.WORKFLOW_INNER_ERROR);
                }

            }
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new AppException(Error.WORKFLOW_INNER_ERROR);
        }
    }

    //将 SalaryAdjusy 业务对象 转化为 通过的 BaseVO,返回前台
    private BaseVO convertBaseVOBySalary(SalaryAdjust salaryAdjust) {
        try{
            BaseVO baseVO = new BaseVO();
            String username = userMapper.getUsernameById(salaryAdjust.getApplication());
            baseVO.setApplication(salaryAdjust.getApplication());
            baseVO.setBusinesstype(Const.BusinessType.SALARY);
            baseVO.setBusinesskey(salaryAdjust.getId().toString());
            baseVO.setApplicationName(username);
            baseVO.setStartTime(salaryAdjust.getCreateTime());
            baseVO.setStatus(Const.BusinessStatus.codeof(salaryAdjust.getStatus().intValue()).getValue());
            baseVO.setBusinessInfo("salaryAdjust:"+salaryAdjust.getAdjustmoney());

            if(salaryAdjust.getStatus() == Const.BusinessStatus.APPLICATION.getCode())
            {
                ProcessInstance   processInstance = getProcessInstanceById(salaryAdjust.getProcessinstanceid());
                Task  task = getTaskByProcessInstance(salaryAdjust.getProcessinstanceid());
                if (processInstance != null && task != null) {
                    baseVO.setProcessInstanceId(processInstance.getId());
                    baseVO.setTaskId(task.getId());
                    baseVO.setApprover(task.getAssignee());
                    return baseVO;
                }
                throw new AppException(Error.WORKFLOW_INNER_ERROR);
            }else{
                HistoricProcessInstance   historicProcessInstance = getHistoryProcessInstanceById(salaryAdjust.getProcessinstanceid());
                HistoricTaskInstance  historicTaskInstance = getHistoricTaskInstance(salaryAdjust.getProcessinstanceid());
                if (historicProcessInstance != null && historicTaskInstance != null) {
                    baseVO.setProcessInstanceId(historicProcessInstance.getId());
                    baseVO.setTaskId(historicTaskInstance.getId());
                    baseVO.setApprover(historicTaskInstance.getAssignee());
                    return baseVO;
                }
                throw new AppException(Error.WORKFLOW_INNER_ERROR);
            }
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new AppException(Error.WORKFLOW_INNER_ERROR);
        }
    }

    //将 任务 对象 转化 为 BaseVO对象，返回前台
    private List<BaseVO> getBaseVOList(List<Task> tasks) {
        try{
            List<BaseVO> baseVOs = Lists.newArrayList();
            for (Task task : tasks) {
                String processInstanceId = task.getProcessInstanceId();
                ProcessInstance processInstance = this.runtimeService//
                        .createProcessInstanceQuery()//
                        .processInstanceId(processInstanceId).singleResult();
                if(processInstance != null){
                    Object obj = this.runtimeService.getVariable(processInstance.getId(), "entry");
                    if(obj instanceof Leave)
                    {
                        baseVOs.add(convertBaseVOByLeave((Leave)obj));
                    }else if(obj instanceof SalaryAdjust)
                    {
                        baseVOs.add(convertBaseVOBySalary((SalaryAdjust)obj));
                    }else {
                        throw new AppException(Error.DATA_VERIFY_ERROR);
                    }
                }
            }
            return baseVOs;
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            logger.debug(e.getMessage());
            throw new AppException(Error.WORKFLOW_INNER_ERROR);
        }
    }

    //通过历史任务 实例  获取 审批结果
    private String getApproveResultByHistoryTaskInstance(HistoricTaskInstance historicTaskInstance) {

        String result = "";
        List<HistoricVariableInstance> historicVariableInstances = historyService
                .createHistoricVariableInstanceQuery().taskId(historicTaskInstance.getId()).list();

        for(HistoricVariableInstance hvi : historicVariableInstances)
        {
            if("isPass".equals(hvi.getVariableName()) || "reApply".equals(hvi.getVariableName()))
            {
                result =hvi.getVariableName()+" : " +hvi.getValue().toString();
            }
        }
        return result;
    }

    //通过历史任务实例 获取 评论信息
    private String getCommentByHistoryTaskInstance(HistoricTaskInstance historicTaskInstance) {

        List<Comment> comments = taskService.getTaskComments(historicTaskInstance.getId());
        String commentStr = "";
        for(Comment comment : comments)
        {
            commentStr += comment.getFullMessage()+"       ";
        }
        return commentStr;
    }

    //任务对象
    private Task getTaskByProcessInstance(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
    }

    //获取 流程实例
    private ProcessInstance getProcessInstanceById(String processInstanceId) {
        return runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    //获取历史任务 实例
    private HistoricTaskInstance getHistoricTaskInstance(String processinstanceid) {
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(processinstanceid).orderByHistoricTaskInstanceEndTime().desc().list();
        if(historicTaskInstances != null)
        {
            return historicTaskInstances.get(0);
        }
        return null;
    }

    //获取历史流程实例
    private HistoricProcessInstance getHistoryProcessInstanceById(String processinstanceid) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceId(processinstanceid).singleResult();
    }
}
