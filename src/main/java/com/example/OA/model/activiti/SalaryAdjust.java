package com.example.OA.model.activiti;


import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public class SalaryAdjust implements Serializable{

    private Integer id;
    //薪资调整金额
    @NotNull
    @Max(value = 10000)
    @Min(value = 1000)
    private BigDecimal adjustmoney;
    //创建时间
    private Date createTime;

    private Date updateTime;

    private String processinstanceid;
    //状态
    private Integer status;
    //描述
    private String description;

    private Integer application;

    public SalaryAdjust() {
    }

    public SalaryAdjust(Integer id,Integer application, BigDecimal adjustmoney, Date createTime, Date updateTime, String processinstanceid, Integer status, String description) {
        this.id = id;
        this.adjustmoney = adjustmoney;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.processinstanceid = processinstanceid;
        this.status = status;
        this.description = description;
        this.application = application;
    }

    public Integer getApplication() {
        return application;
    }

    public void setApplication(Integer application) {
        this.application = application;
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