package com.example.OA.dao;

import com.example.OA.model.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    //----------------------------------------------------
    Role getByRolename(String roleName);

    List<Role> getByRoleIds(@Param("roleIds") List<Integer> roleIds);

    List<Role> getAll();
}