package com.example.OA.model.VO;

import com.example.OA.model.VO.CommentVO;
import com.example.OA.model.activiti.Leave;
import com.example.OA.mvc.common.Const;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by aa on 2017/11/24.
 */
public class LeaveVO implements Serializable {

    private Integer id;
    //请假类型

    private String leaveType;
    //请假天数

    private Integer leaveNumber;
    //请假原因
    private String reason;
    //状态
    private String status;
    //开始时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    //结束时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    //对应的流程实例ID
    private String processinstanceid;

    //申请人姓名
    private String applicationName;

    public LeaveVO() {
    }

    public LeaveVO(Leave leave ,String applicationName) {
        this.id = leave.getId();
        this.leaveType = leave.getLeaveType();
        this.leaveNumber = leave.getLeaveNumber();
        this.reason = leave.getReason();
        this.status = Const.BusinessStatus.codeof(leave.getStatus()).getValue();
        this.startTime = leave.getStartTime();
        this.endTime = leave.getEndTime();
        this.createTime = leave.getCreateTime();
        this.updateTime = leave.getUpdateTime();
        this.processinstanceid = leave.getProcessinstanceid();
        this.applicationName = applicationName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public Integer getLeaveNumber() {
        return leaveNumber;
    }

    public void setLeaveNumber(Integer leaveNumber) {
        this.leaveNumber = leaveNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getProcessinstanceid() {
        return processinstanceid;
    }

    public void setProcessinstanceid(String processinstanceid) {
        this.processinstanceid = processinstanceid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
