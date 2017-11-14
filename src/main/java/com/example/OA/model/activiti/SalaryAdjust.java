package com.example.OA.model.activiti;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public class SalaryAdjust extends BaseVO implements Serializable{

    private Integer id;

    private BigDecimal adjustmoney;

    private Date createTime;

    private Date updateTime;

    private String processinstanceid;

    private Integer status;

    private String description;

    public SalaryAdjust() {
    }

    public SalaryAdjust(Integer id,Integer application,String applicationName,String businessType, BigDecimal adjustmoney, Date createTime, Date updateTime, String processinstanceid, Integer status, String description) {
        this.setApplication(application);
        this.setApplicationName(applicationName);
        this.setBusinesstype(businessType);

        this.id = id;
        this.adjustmoney = adjustmoney;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.processinstanceid = processinstanceid;
        this.status = status;
        this.description = description;
    }

    public SalaryAdjust(Integer id,Integer application,String applicationName,String businessType, BigDecimal adjustmoney, Date createTime, Date updateTime, String processinstanceid, Integer status, String description,byte[] bytes) {
        this.setApplication(application);
        this.setApplicationName(applicationName);
        this.setBusinesstype(businessType);
        System.out.println("你是个什么东西：：："+new String(bytes));
        this.id = id;
        this.adjustmoney = adjustmoney;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.processinstanceid = processinstanceid;
        this.status = status;
        this.description = description;
    }

    public SalaryAdjust(Integer id,Integer application,String applicationName,String businessType, BigDecimal adjustmoney, Date createTime, Date updateTime, String processinstanceid, Integer status, String description,String businessKey) {
        this.setApplication(application);
        this.setApplicationName(applicationName);
        this.setBusinesstype(businessType);
        this.setBusinesskey(businessKey);
        this.id = id;
        this.adjustmoney = adjustmoney;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.processinstanceid = processinstanceid;
        this.status = status;
        this.description = description;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAdjustmoney() {
        return adjustmoney;
    }

    public void setAdjustmoney(BigDecimal adjustmoney) {
        this.adjustmoney = adjustmoney;
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
        this.processinstanceid = processinstanceid == null ? null : processinstanceid.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

}