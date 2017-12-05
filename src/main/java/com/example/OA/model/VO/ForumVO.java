package com.example.OA.model.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by aa on 2017/12/4.
 */
public class ForumVO implements Serializable{

    private Integer id;

    private String forumName;

    private String description;

    private Integer sorts;

    private Integer topCount;

    private Integer replyCount;

    private String lastTopicName;

    private Integer lastTopic;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Integer getLastTopic() {
        return lastTopic;
    }

    public void setLastTopic(Integer lastTopic) {
        this.lastTopic = lastTopic;
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
        this.forumName = forumName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getLastTopicName() {
        return lastTopicName;
    }

    public void setLastTopicName(String lastTopicName) {
        this.lastTopicName = lastTopicName;
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
