package com.example.OA.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

public class Topic implements Serializable {
    private Integer id;

    @Size(max = 100)
    @NotEmpty
    private String title;

    @Size(max = 400)
    @NotEmpty
    private String content;

    private Date createTime;

    private Date updateTime;

    private Short status;

    @NotEmpty
    private Integer author;

    @NotEmpty
    private String ip;

    private Integer replyCount;

    private Integer forumId;

    private Integer lastReply;

    public Topic(Integer id, String title, String content, Date createTime, Date updateTime, Short status, Integer author, String ip, Integer replyCount, Integer forumId, Integer lastReply) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.status = status;
        this.author = author;
        this.ip = ip;
        this.replyCount = replyCount;
        this.forumId = forumId;
        this.lastReply = lastReply;
    }

    public Topic() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
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

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public Integer getForumId() {
        return forumId;
    }

    public void setForumId(Integer forumId) {
        this.forumId = forumId;
    }

    public Integer getLastReply() {
        return lastReply;
    }

    public void setLastReply(Integer lastReply) {
        this.lastReply = lastReply;
    }
}