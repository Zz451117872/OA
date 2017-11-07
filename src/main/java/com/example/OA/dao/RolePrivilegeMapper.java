package com.example.OA.dao;

import com.example.OA.model.Privilege;
import com.example.OA.model.RolePrivilegeKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RolePrivilegeMapper {
    int deleteByPrimaryKey(RolePrivilegeKey key);

    int insert(RolePrivilegeKey record);

    int insertSelective(RolePrivilegeKey record);

    //--------------------------------------

    int deleteByRoleId(Integer roleId);

    int deleteByRoleidAndPrivilegeid(@Param("roleId") Integer roleId, @Param("privilegeId")Integer privilegeId);

    List<Privilege> getByRoleId(Integer roleId);

    int deleteByPrivilegeId(Integer privilegeId);

    List<Integer> getPrivilegeIdByRoleid(Integer roleId);
}