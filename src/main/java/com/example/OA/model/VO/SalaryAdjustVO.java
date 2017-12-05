package com.example.OA.model.VO;

import com.example.OA.model.VO.CommentVO;
import com.example.OA.model.activiti.SalaryAdjust;
import com.example.OA.mvc.common.Const;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by aa on 2017/11/24.
 */
public class SalaryAdjustVO implements Serializable {

    private Integer id;

    private BigDecimal adjustmoney;
    //创建时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String processinstanceid;
    //状态
    private String status;
    //描述
    private String description;

    private String applicationName;

    public SalaryAdjustVO() {
    }

    public SalaryAdjustVO(SalaryAdjust salary ,String applicationName) {
        this.id = salary.getId();
        this.adjustmoney = salary.getAdjustmoney();
        this.createTime = salary.getCreateTime();
        this.updateTime = salary.getUpdateTime();
        this.processinstanceid = salary.getProcessinstanceid();
        this.status = Const.BusinessStatus.codeof(salary.getStatus()).getValue();
        this.description = salary.getDescription();
        this.applicationName = applicationName;
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
        this.processinstanceid = processinstanceid;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
