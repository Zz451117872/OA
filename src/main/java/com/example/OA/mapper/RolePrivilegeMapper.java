package com.example.OA.mapper;

import com.example.OA.model.Privilege;
import com.example.OA.model.RolePrivilegeKey;

import java.util.List;

public interface RolePrivilegeMapper {
    int deleteByPrimaryKey(RolePrivilegeKey key);

    int insert(RolePrivilegeKey record);

    int insertSelective(RolePrivilegeKey record);

    //--------------------------------------

    int deleteByRoleId(Integer roleId);

    int deleteByRoleidAndPrivilegeid(Integer roleId, Integer privilegeId);

    List<Privilege> getByRoleId(Integer roleId);
}