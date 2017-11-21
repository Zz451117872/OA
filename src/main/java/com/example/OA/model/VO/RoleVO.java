package com.example.OA.model.VO;

import com.example.OA.model.Role;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by aa on 2017/11/21.
 */
public class RoleVO implements Serializable{

    private Integer id;

    private String roleName;

    private String description;

    private Date createTime;

    private Date updateTime;

    private Boolean checked;

    public RoleVO() {
    }

    public RoleVO(Role role, Boolean checked) {
        this.id = role.getId();
        this.roleName = role.getRoleName();
        this.description = role.getDescription();
        this.createTime = role.getCreateTime();
        this.updateTime = role.getUpdateTime();
        this.checked = checked;
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
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
