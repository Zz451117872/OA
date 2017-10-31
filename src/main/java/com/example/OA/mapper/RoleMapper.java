package com.example.OA.mapper;

import com.example.OA.model.Role;

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

    List<Role> getByRoleIds(List<Integer> roleIds);
}