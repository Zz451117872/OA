package com.example.OA.mapper;

import com.example.OA.model.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //-----------------------------------------------------
    User getByUsername(String username);

    int deleteByPartId(Integer partId);
}