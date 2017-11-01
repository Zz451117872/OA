package com.example.OA.model;

import java.io.Serializable;
import java.util.Date;

public class Privilege implements Serializable {
    private Integer id;

    private String privilegeName;

    private String url;

    private Integer parent;

    private Date createTime;

    private Date updateTime;

    public Privilege(Integer id, String privilegeName, String url, Integer parent, Date createTime, Date updateTime) {
        this.id = id;
        this.privilegeName = privilegeName;
        this.url = url;
        this.parent = parent;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Privilege() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName == null ? null : privilegeName.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
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