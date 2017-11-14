package com.example.OA.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class User implements Serializable{
    private Integer id;

    private String username;

    private String password;

    private Integer partId;

    private Date createTime;

    private Date updateTime;

    private BigDecimal salary;

    public User(Integer id, String username, String password, Integer partId, Date createTime, Date updateTime,BigDecimal salary) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.partId = partId;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.salary = salary;
    }

    public User() {
        super();
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
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