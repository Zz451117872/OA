package com.example.OA.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

public class Reply implements Serializable {
    private Integer id;

    @Size(max = 100)
    private String title;

    @Size(max = 400)
    private String content;

    private Short status;

    private Date replayTime;

    @NotEmpty
    private Integer author;

    @NotEmpty
    private String ip;

    @NotEmpty
    private Integer topicId;

    public Reply(Integer id, String title, String content, Short status, Date replayTime, Integer author, String ip, Integer topicId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
        this.replayTime = replayTime;
        this.author = author;
        this.ip = ip;
        this.topicId = topicId;
    }

    public Reply() {
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

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Date getReplayTime() {
        return replayTime;
    }

    public void setReplayTime(Date replayTime) {
        this.replayTime = replayTime;
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

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }
}