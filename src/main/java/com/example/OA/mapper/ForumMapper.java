package com.example.OA.mapper;

import com.example.OA.model.Forum;

public interface ForumMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Forum record);

    int insertSelective(Forum record);

    Forum selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Forum record);

    int updateByPrimaryKey(Forum record);
}