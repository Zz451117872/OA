package com.example.OA.model;

import java.util.Date;

/**
 * Created by aa on 2017/11/3.
 */
public class Dispose {

    private Integer id;
    private Integer leaveId;
    private Integer auditer;
    private String auditInformation;
    private Date auditTime;

    public Dispose() {
    }

    public Dispose(Integer id, Integer leaveId, Integer auditer, String auditInformation, Date auditTime) {
        this.id = id;
        this.leaveId = leaveId;
        this.auditer = auditer;
        this.auditInformation = auditInformation;
        this.auditTime = auditTime;
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

    public Integer getAuditer() {
        return auditer;
    }

    public void setAuditer(Integer auditer) {
        this.auditer = auditer;
    }

    public String getAuditInformation() {
        return auditInformation;
    }

    public void setAuditInformation(String auditInformation) {
        this.auditInformation = auditInformation;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }
}
