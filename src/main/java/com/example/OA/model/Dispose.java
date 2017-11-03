package com.example.OA.model;

import java.io.Serializable;
import java.util.Date;

public class Dispose implements Serializable{
    private Integer id;

    private Integer leaveId;

    private String auditer;

    private String information;

    private Date auditTime;

    public Dispose(Integer id, Integer leaveId, String auditer, String information, Date auditTime) {
        this.id = id;
        this.leaveId = leaveId;
        this.auditer = auditer;
        this.information = information;
        this.auditTime = auditTime;
    }

    public Dispose() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(Integer leaveId) {
        this.leaveId = leaveId;
    }

    public String getAuditer() {
        return auditer;
    }

    public void setAuditer(String auditer) {
        this.auditer = auditer == null ? null : auditer.trim();
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information == null ? null : information.trim();
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }
}