package com.example.OA.model.VO;

import com.example.OA.model.Privilege;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by aa on 2017/11/21.
 */
public class PrivilegeVO implements Serializable {

    private Integer id;

    private String privilegeName;

    private String url;

    private String parentName;

    private Date createTime;

    private Date updateTime;

    private List<PrivilegeVO> childs;

    private Boolean checked;

    public PrivilegeVO() {
    }

    public PrivilegeVO(Privilege privilege,String parentName, Boolean checked) {
        this.id = privilege.getId();
        this.privilegeName = privilege.getPrivilegeName();
        this.url = privilege.getUrl();
        this.parentName = parentName;
        this.createTime = privilege.getCreateTime();
        this.updateTime = privilege.getUpdateTime();
        this.checked = checked;
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
        this.privilegeName = privilegeName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
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

    public List<PrivilegeVO> getChilds() {
        return childs;
    }

    public void setChilds(List<PrivilegeVO> childs) {
        this.childs = childs;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
