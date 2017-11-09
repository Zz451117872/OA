package com.example.OA.model.activiti;

public class ActGroupUserKey {
    private String userId;

    private String groupId;

    public ActGroupUserKey(String userId, String groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public ActGroupUserKey() {
        super();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId == null ? null : groupId.trim();
    }
}