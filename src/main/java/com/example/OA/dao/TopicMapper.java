package com.example.OA.dao;

import com.example.OA.model.Topic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TopicMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Topic record);

    int insertSelective(Topic record);

    Topic selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Topic record);

    int updateByPrimaryKey(Topic record);

    //--------------

    List<Topic> getAllByForum(Integer forumId);

    List<Topic> getAllByAuthor(Integer userId);

    Topic getByIdOrName(@Param("topicId") Integer topicId, @Param("topicName")String topicName);
}