package com.example.OA.model.VO;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by aa on 2017/11/10.
 */
public class BaseVO implements Serializable{

    //申请人id
    private Integer application;
    //申请人姓名
    private String applicationName;
    //业务类型
    private String businesstype;
    //对应业务的id
    private String businesskey;
    //审批状态
    private String status;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date claimTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    //审批人
    private String approver;

    //业务信息
    private String businessInfo;
    //任务Id
    private String taskId;
    //流程实例Id
    private String processInstanceId;
    //历史流程实例id
//    private String historicProcessInstanceId;
    //流程定义id
//    private String processDefinitionId;
    //历史任务id
//    private String historicTaskInstanceId;

    public String getBusinessInfo() {
        return businessInfo;
    }

    public void setBusinessInfo(String businessInfo) {
        this.businessInfo = businessInfo;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getClaimTime() {
        return claimTime;
    }

    public void setClaimTime(Date claimTime) {
        this.claimTime = claimTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getApplication() {
        return application;
    }

    public void setApplication(Integer application) {
        this.application = application;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getBusinesstype() {
        return businesstype;
    }

    public void setBusinesstype(String businesstype) {
        this.businesstype = businesstype;
    }

    public String getBusinesskey() {
        return businesskey;
    }

    public void setBusinesskey(String businesskey) {
        this.businesskey = businesskey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

}
