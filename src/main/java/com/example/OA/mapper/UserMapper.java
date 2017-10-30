package com.example.OA.mapper;

import com.example.OA.model.User;
import org.apache.ibatis.annotations.*;

/**
 * Created by aa on 2017/10/30.
 */
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    @Results({
            @Result(property = "id",  column = "id"),
            @Result(property = "password", column = "password")
    })
    User getByUsername(String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    @Results({
            @Result(property = "username",  column = "username"),
            @Result(property = "password", column = "password")
    })
    User getOne(Integer id);

    @Insert("INSERT INTO user(username,password) VALUES(#{username}, #{password})")
    void insert(User user);

    @Update("UPDATE user SET username=#{username},password=#{password} WHERE id =#{id}")
    void update(User user);

    @Delete("DELETE FROM user WHERE id =#{id}")
    void delete(Integer id);
}
