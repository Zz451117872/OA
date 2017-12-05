package com.example.OA.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public class Role implements Serializable {
    private Integer id;

    @NotNull
    private String roleName;

    private String description;

    private Date createTime;

    private Date updateTime;

    public Role(Integer id, String roleName, String description, Date createTime, Date updateTime) {
        this.id = id;
        this.roleName = roleName;
        this.description = description;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Role() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
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