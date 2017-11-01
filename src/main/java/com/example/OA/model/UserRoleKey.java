package com.example.OA.model;

import java.io.Serializable;

public class UserRoleKey implements Serializable {
    private Integer userId;

    private Integer roleId;

    public UserRoleKey(Integer userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public UserRoleKey() {
        super();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}