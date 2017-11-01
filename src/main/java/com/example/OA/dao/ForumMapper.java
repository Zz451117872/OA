package com.example.OA.dao;

import com.example.OA.model.Forum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ForumMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Forum record);

    int insertSelective(Forum record);

    Forum selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Forum record);

    int updateByPrimaryKey(Forum record);


    //---------------------------
    Forum getByForumName(String forumName);

    List<Forum> getAll();

    Forum getByIdOrName(@Param("forumId") Integer forumId, @Param("forumName")String forumName);
}