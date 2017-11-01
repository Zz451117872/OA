package com.example.OA.model;

import java.io.Serializable;

public class RolePrivilegeKey implements Serializable {
    private Integer roleId;

    private Integer privilegeId;

    public RolePrivilegeKey(Integer roleId, Integer privilegeId) {
        this.roleId = roleId;
        this.privilegeId = privilegeId;
    }

    public RolePrivilegeKey() {
        super();
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(Integer privilegeId) {
        this.privilegeId = privilegeId;
    }
}