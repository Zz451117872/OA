package com.example.OA.model.VO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by aa on 2017/11/25.
 */
public class HistoryTaskVO implements Serializable{

    //任务Id
    private String taskId;

    //申请人姓名
    private String applicationName;
    //业务类型
    private String businesstype;

    //业务信息
    private String businessInfo;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date claimTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    //经手人
    private String approver;

    //处理结果
    private String approveResult;

    //处理意见
    private String comment;


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public String getBusinessInfo() {
        return businessInfo;
    }

    public void setBusinessInfo(String businessInfo) {
        this.businessInfo = businessInfo;
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

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getApproveResult() {
        return approveResult;
    }

    public void setApproveResult(String approveResult) {
        this.approveResult = approveResult;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
