package com.example.OA.model;

import java.io.Serializable;
import java.util.Date;

public class Forum implements Serializable {
    private Integer id;

    private String forumName;

    private String description;

    private Integer sorts;

    private Integer topCount;

    private Integer replyCount;

    private Integer lastTopic;

    private Date createTime;

    private Date updateTime;

    public Forum(Integer id, String forumName, String description, Integer sorts, Integer topCount, Integer replyCount, Integer lastTopic, Date createTime, Date updateTime) {
        this.id = id;
        this.forumName = forumName;
        this.description = description;
        this.sorts = sorts;
        this.topCount = topCount;
        this.replyCount = replyCount;
        this.lastTopic = lastTopic;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Forum() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName == null ? null : forumName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getSorts() {
        return sorts;
    }

    public void setSorts(Integer sorts) {
        this.sorts = sorts;
    }

    public Integer getTopCount() {
        return topCount;
    }

    public void setTopCount(Integer topCount) {
        this.topCount = topCount;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public Integer getLastTopic() {
        return lastTopic;
    }

    public void setLastTopic(Integer lastTopic) {
        this.lastTopic = lastTopic;
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
}