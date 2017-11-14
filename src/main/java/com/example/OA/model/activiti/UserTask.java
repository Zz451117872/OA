package com.example.OA.model.activiti;

import java.io.Serializable;

public class UserTask implements Serializable{

    private Integer id;
    //流程定义
    private String procdefkey;
    //流程定义名
    private String procdefname;
    //任务定义key
    private String taskdefkey;
    //任务名
    private String taskname;
    //任务类型
    private String tasktype;
    //候选名
    private String candidateName;
    //候选id
    private String candidateIds;

    public UserTask() {
    }

    public UserTask(Integer id, String procdefkey, String procdefname, String taskdefkey, String taskname, String tasktype, String candidateName, String candidateIds) {
        this.id = id;
        this.procdefkey = procdefkey;
        this.procdefname = procdefname;
        this.taskdefkey = taskdefkey;
        this.taskname = taskname;
        this.tasktype = tasktype;
        this.candidateName = candidateName;
        this.candidateIds = candidateIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProcdefkey() {
        return procdefkey;
    }

    public void setProcdefkey(String procdefkey) {
        this.procdefkey = procdefkey == null ? null : procdefkey.trim();
    }

    public String getProcdefname() {
        return procdefname;
    }

    public void setProcdefname(String procdefname) {
        this.procdefname = procdefname == null ? null : procdefname.trim();
    }

    public String getTaskdefkey() {
        return taskdefkey;
    }

    public void setTaskdefkey(String taskdefkey) {
        this.taskdefkey = taskdefkey == null ? null : taskdefkey.trim();
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname == null ? null : taskname.trim();
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype == null ? null : tasktype.trim();
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName == null ? null : candidateName.trim();
    }

    public String getCandidateIds() {
        return candidateIds;
    }

    public void setCandidateIds(String candidateIds) {
        this.candidateIds = candidateIds == null ? null : candidateIds.trim();
    }
}