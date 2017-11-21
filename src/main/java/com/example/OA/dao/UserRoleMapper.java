package com.example.OA.dao;

import com.example.OA.model.UserRoleKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRoleMapper {
    int deleteByPrimaryKey(UserRoleKey key);

    int insert(UserRoleKey record);

    int insertSelective(UserRoleKey record);

    //-------------------------------------------------------
    int deleteByRoleId(Integer roleId);

    int deleteByRoleidAndUserid(@Param("userId") Integer userId, @Param("roleId")Integer roleId);

    List<Integer> getRoleidByUserid(Integer userId);

    int deleteByRoleidsAndUserid(@Param("userId")Integer userId, @Param("roleIds")List<Integer> roleIds);

    void deleteAll();

    void deleteByUserId(Integer userId);

    UserRoleKey getByUserAndRole(@Param("userId")Integer userId, @Param("roleId")Integer roleId);
}