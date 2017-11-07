package com.example.OA.model.activiti;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by aa on 2017/11/6.
 */
public class TaskBean implements Serializable {

    private String id;
    private String name;
    private Date createTime;
    private String assignee;

    public TaskBean() {
    }

    public TaskBean(String id, String name, Date createTime, String assignee) {
        this.id = id;
        this.name = name;
        this.createTime = createTime;
        this.assignee = assignee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
