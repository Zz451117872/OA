package com.example.OA.model.VO;

import com.example.OA.model.Reply;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pagehelper.PageInfo;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by aa on 2017/12/2.
 */
public class TopicVO implements Serializable{

    private Integer id;

    private String title;

    private String content;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String status;

    private String statusName;
    private String authorName;

    private String ip;

    private Integer replyCount;

    private String forumName;

    private Integer lastReply;

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
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
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
    }

    public Integer getLastReply() {
        return lastReply;
    }

    public void setLastReply(Integer lastReply) {
        this.lastReply = lastReply;
    }
}
