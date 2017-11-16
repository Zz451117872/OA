package com.example.OA.model.activiti;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by aa on 2017/11/6.
 */
public class TaskBean implements Serializable {

    private String id;

    @NotEmpty
    private String name;
    private Date createTime;
    private String assignee;
    private String owner;
    private String processInstanceId;
    private String executionId;
    private String status;

    public TaskBean() {
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TaskBean(String id, String name, Date createTime, String assignee, String owner, String processInstanceId, String executionId, String status) {
        this.id = id;
        this.name = name;
        this.createTime = createTime;
        this.assignee = assignee;
        this.owner = owner;
        this.processInstanceId = processInstanceId;
        this.executionId = executionId;
        this.status = status;
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
