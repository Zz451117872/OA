package com.example.OA.mapper;

import com.example.OA.model.UserRoleKey;

import java.util.List;

public interface UserRoleMapper {
    int deleteByPrimaryKey(UserRoleKey key);

    int insert(UserRoleKey record);

    int insertSelective(UserRoleKey record);

    //-------------------------------------------------------
    int deleteByRoleId(Integer roleId);

    int deleteByRoleidAndUserid(Integer userId, Integer roleId);

    List<Integer> getRoleidByUserid(Integer userId);
}