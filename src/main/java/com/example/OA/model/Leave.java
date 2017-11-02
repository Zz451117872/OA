package com.example.OA.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by aa on 2017/11/2.
 */
public class Leave  implements Serializable{

    private Integer id;
    private String leaveType;
    private Integer leaveNumber;
    private String reason;
    private Integer status;
    private Integer applicant;
    private Date startTime;
    private Date endTime;
    private Date createTime;
    private Date updateTime;

    public Leave() {
    }

    public Leave(Integer id, String leaveType, Integer leaveNumber, String reason, Integer status, Integer applicant, Date startTime, Date endTime, Date createTime, Date updateTime) {
        this.id = id;
        this.leaveType = leaveType;
        this.leaveNumber = leaveNumber;
        this.reason = reason;
        this.status = status;
        this.applicant = applicant;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTime = createTime;
        this.updateTime = updateTime;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getApplicant() {
        return applicant;
    }

    public void setApplicant(Integer applicant) {
        this.applicant = applicant;
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
}

