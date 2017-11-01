package com.example.OA.service.manager;

import com.example.OA.dao.*;
import com.example.OA.model.Privilege;
import com.example.OA.model.Role;
import com.example.OA.model.RolePrivilegeKey;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by aa on 2017/10/31.
 */
@Service
public class RoleService {

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    UserRoleMapper userRoleMapper;

    @Autowired
    RolePrivilegeMapper rolePrivilegeMapper;

    @Autowired
    PrivilegeMapper privilegeMapper;

    public String addRole(Role role) {
        if(role != null) {
            String roleName = role.getRoleName();
            if (StringUtils.isNotBlank(roleName) && roleMapper.getByRolename(roleName) == null) {
                role.setCreateTime(new Date());
                roleMapper.insert(role);
                return roleName;
            }
            throw new AppException(Error.EXISTSED);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public String updateRole(Role role) {
        if(role != null) {
            if (roleMapper.selectByPrimaryKey(role.getId()) != null) {
                role.setUpdateTime(new Date());
                roleMapper.updateByPrimaryKeySelective(role);
                return role.getRoleName();
            }
            throw new AppException(Error.NO_EXISTS);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public String deleteById(Integer roleId) {
        //删除角色，要级联删除 用户与角色关联信息，权限与角色关联信息
        if(roleId != null) {
            if (roleMapper.selectByPrimaryKey(roleId) != null) {
               int result = roleMapper.deleteByPrimaryKey(roleId);
                if(result >= 1)
                {
                    userRoleMapper.deleteByRoleId(roleId);
                    rolePrivilegeMapper.deleteByRoleId(roleId);
                    return "success";
                }
                throw new AppException(Error.UNKNOW_EXCEPTION);
            }
            throw new AppException(Error.NO_EXISTS);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public int endowPrivilegeToRole(Integer roleId, String privilegeIds) {
        if(roleId != null && StringUtils.isNotBlank(privilegeIds))
        {
            Role role = roleMapper.selectByPrimaryKey(roleId);
            if(role == null) throw new AppException(Error.NO_EXISTS,"role not exist");
            int result = 0;
            String[] privilegeArr = privilegeIds.split(",");
            for(int i=0; i<privilegeArr.length; i++)
            {
                Integer privilegeId = Integer.parseInt(privilegeArr[i]);
                if(privilegeMapper.selectByPrimaryKey(privilegeId) != null)
                {
                    //授予该角色权限时，要判断该角色是否已拥有该权限
                    RolePrivilegeKey rolePrivilegeKey = new RolePrivilegeKey(roleId,privilegeId);
                    rolePrivilegeMapper.insert(rolePrivilegeKey);
                    result ++;
                }
            }
            return result;
        }
        throw new AppException(Error.PARAMS_ERROR,"params error");
    }

    public int takebackPrivilegeFormRole(Integer roleId, String privilegeIds) {
        if(roleId != null && StringUtils.isNotBlank(privilegeIds))
        {
            Role role = roleMapper.selectByPrimaryKey(roleId);
            if(role == null) throw new AppException(Error.NO_EXISTS,"role not exist");
            int result = 0;
            String[] privilegeArr = privilegeIds.split(",");
            for(int i=0; i<privilegeArr.length; i++)
            {
                Integer privilegeId = Integer.parseInt(privilegeArr[i]);
                if(privilegeMapper.selectByPrimaryKey(privilegeId) != null)
                {
                    //收回角色 权限时，要判断该角色是否拥有该权限
                    rolePrivilegeMapper.deleteByRoleidAndPrivilegeid(roleId,privilegeId);
                    result ++;
                }
            }
            return result;
        }
        throw new AppException(Error.PARAMS_ERROR,"params error");
    }

    public List<Privilege> getAllPrivilegeByRoleId(Integer roleId) {
        if(roleId != null)
        {
            return rolePrivilegeMapper.getByRoleId(roleId);
        }
        throw new AppException(Error.PARAMS_ERROR,"params error");
    }
}
