package com.example.OA.model.activiti;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by aa on 2017/11/6.
 */
public class ProcessDefinitionBean implements Serializable{

    private String id;
    private String key;
    private String pro_defi_name;
    private String pro_devl_name;
    private Date pro_devl_time;
    private String version;
    private String devloyment_id;

    public ProcessDefinitionBean() {
    }

    public ProcessDefinitionBean(String id, String key, String pro_defi_name, String pro_devl_name, Date pro_devl_time, String version, String devloyment_id) {
        this.id = id;
        this.key = key;
        this.pro_defi_name = pro_defi_name;
        this.pro_devl_name = pro_devl_name;
        this.pro_devl_time = pro_devl_time;
        this.version = version;
        this.devloyment_id = devloyment_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPro_defi_name() {
        return pro_defi_name;
    }

    public void setPro_defi_name(String pro_defi_name) {
        this.pro_defi_name = pro_defi_name;
    }

    public String getPro_devl_name() {
        return pro_devl_name;
    }

    public void setPro_devl_name(String pro_devl_name) {
        this.pro_devl_name = pro_devl_name;
    }

    public Date getPro_devl_time() {
        return pro_devl_time;
    }

    public void setPro_devl_time(Date pro_devl_time) {
        this.pro_devl_time = pro_devl_time;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDevloyment_id() {
        return devloyment_id;
    }

    public void setDevloyment_id(String devloyment_id) {
        this.devloyment_id = devloyment_id;
    }
}

