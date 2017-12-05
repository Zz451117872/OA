package com.example.OA.dao;

import com.example.OA.model.Reply;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReplyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Reply record);

    int insertSelective(Reply record);

    Reply selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Reply record);

    int updateByPrimaryKey(Reply record);

    //-------------------------------
    int setStatusById(@Param("replyId") Integer replyId, @Param("status")Short status);

    List<Reply> getAllReplyByUser(Integer userId);

    List<Reply> getAllByTopic(Integer topicId);

    List<Reply> getAll();

    List<Reply> getAllReplyByTopic(Integer topicId);
}