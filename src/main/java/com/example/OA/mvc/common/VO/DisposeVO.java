package com.example.OA.mvc.common.VO;

import java.io.Serializable;
import java.util.Date;


/**
 * Created by aa on 2017/11/3.
 */
public class DisposeVO implements Serializable{

    private Integer auditerId;
    private String auditName;
    private String auditInformation;
    private Date auditTime;

    public DisposeVO() {
    }

    public DisposeVO(Integer auditerId, String auditName, String auditInformation, Date auditTime) {
        this.auditerId = auditerId;
        this.auditName = auditName;
        this.auditInformation = auditInformation;
        this.auditTime = auditTime;
    }

    public Integer getAuditerId() {
        return auditerId;
    }

    public void setAuditerId(Integer auditerId) {
        this.auditerId = auditerId;
    }

    public String getAuditName() {
        return auditName;
    }

    public void setAuditName(String auditName) {
        this.auditName = auditName;
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
