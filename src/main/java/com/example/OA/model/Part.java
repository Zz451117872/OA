package com.example.OA.model;

import java.io.Serializable;
import java.util.Date;

public class Part implements Serializable {
    private Integer id;

    private String partName;

    private String description;

    private Date createTime;

    private Date updateTime;

    public Part(Integer id, String partName, String description, Date createTime, Date updateTime) {
        this.id = id;
        this.partName = partName;
        this.description = description;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Part() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName == null ? null : partName.trim();
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