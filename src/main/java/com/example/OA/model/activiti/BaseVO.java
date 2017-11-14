package com.example.OA.model.activiti;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by aa on 2017/11/10.
 */
public class BaseVO implements Serializable{

    // 申请的标题
    private String title;

    private Integer application;

    private String applicationName;

    private String businesstype;

    //对应业务的id
    private String businesskey;


    //-- 临时属性 --//

    // 流程任务

    private TaskBean taskBean;


    private Map<String, Object> variables;

    // 运行中的流程实例
    @JsonBackReference
    private ProcessInstance processInstance;

    // 历史的流程实例
    @JsonBackReference
    private HistoricProcessInstance historicProcessInstance;

    // 流程定义
    @JsonBackReference
    private ProcessDefinition processDefinition;

    @JsonBackReference
    private HistoricTaskInstance historicTaskInstance;

    @Transient
    public HistoricTaskInstance getHistoricTaskInstance() {
        return historicTaskInstance;
    }

    public void setHistoricTaskInstance(HistoricTaskInstance historicTaskInstance) {
        this.historicTaskInstance = historicTaskInstance;
    }

    public Integer getApplication() {
        return application;
    }

    public void setApplication(Integer application) {
        this.application = application;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getBusinesstype() {
        return businesstype;
    }

    public void setBusinesstype(String businesstype) {
        this.businesstype = businesstype;
    }

    public String getBusinesskey() {
        return businesskey;
    }

    public void setBusinesskey(String businesskey) {
        this.businesskey = businesskey;
    }


    public TaskBean getTaskBean() {
        return taskBean;
    }

    public void setTaskBean(TaskBean taskBean) {
        this.taskBean = taskBean;
    }


    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    @Transient
    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    @Transient
    public HistoricProcessInstance getHistoricProcessInstance() {
        return historicProcessInstance;
    }

    public void setHistoricProcessInstance(HistoricProcessInstance historicProcessInstance) {
        this.historicProcessInstance = historicProcessInstance;
    }

    @Transient
    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(ProcessDefinition processDefinition) {
        this.processDefinition = processDefinition;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }




}
