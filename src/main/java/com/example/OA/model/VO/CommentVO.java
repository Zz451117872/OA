package com.example.OA.model.VO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by aa on 2017/11/11.
 */
public class CommentVO implements Serializable{

    // 评论人
    private String userName;

    // 评论内容
    private String content;

    // 评论时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date time;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
