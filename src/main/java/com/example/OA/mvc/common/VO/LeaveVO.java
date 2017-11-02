package com.example.OA.mvc.common.VO;


import java.io.Serializable;
import java.util.List;

/**
 * Created by aa on 2017/11/3.
 */
public class LeaveVO implements Serializable{

    private String leaveType;
    private Integer leaveNumber;
    private String reason;
    private Integer status;
    private Integer applicationId;
    private String applicationName;
    private List<DisposeVO> disposes;

    public LeaveVO() {
    }

    public LeaveVO(String leaveType, Integer leaveNumber, String reason, Integer status, Integer applicationId, String applicationName, List<DisposeVO> disposes) {
        this.leaveType = leaveType;
        this.leaveNumber = leaveNumber;
        this.reason = reason;
        this.status = status;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.disposes = disposes;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public Integer getLeaveNumber() {
        return leaveNumber;
    }

    public void setLeaveNumber(Integer leaveNumber) {
        this.leaveNumber = leaveNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public List<DisposeVO> getDisposes() {
        return disposes;
    }

    public void setDisposes(List<DisposeVO> disposes) {
        this.disposes = disposes;
    }
}
