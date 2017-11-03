package com.example.OA.dao;

import com.example.OA.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    User getByIdOrUsername(@Param("userId") Integer userId,@Param("username") String username);

    List<User> getByUsernames(@Param("usernames")List<String> usernames);
}