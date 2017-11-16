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
import org.springframework.transaction.annotation.Transactional;

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

    //添加一个角色
    public String addRole(Role role) {
        try{
            String roleName = role.getRoleName(); //角色名唯一
            if (roleMapper.getByRolename(roleName) == null) {
                role.setCreateTime(new Date());
                roleMapper.insert(role);
                return roleName;
            }
            throw new AppException(Error.TARGET_EXISTSED,"角色名已存在");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e) {
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    //更新一个角色
    public void updateRole(Role role) {
        try{
            if (roleMapper.selectByPrimaryKey(role.getId()) != null) {
                if(roleMapper.getByRolename(role.getRoleName()) == null) {
                    role.setUpdateTime(new Date());
                    roleMapper.updateByPrimaryKeySelective(role);
                    return;
                }
                throw new AppException(Error.TARGET_NO_EXISTS,"角色名存在");
            }
            throw new AppException(Error.TARGET_NO_EXISTS,"角色不存在");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e) {
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    //删除一个角色
    @Transactional
    public void deleteById(Integer roleId) {
        //删除角色，要级联删除 用户与角色关联信息，权限与角色关联信息
        try{
            if (roleMapper.selectByPrimaryKey(roleId) != null) {
               int result = roleMapper.deleteByPrimaryKey(roleId);
                if(result >= 1)
                {
                    userRoleMapper.deleteByRoleId(roleId);
                    rolePrivilegeMapper.deleteByRoleId(roleId);
                    return ;
                }
                throw new AppException(Error.UNKNOW_EXCEPTION,"database inner error");
            }
            throw new AppException(Error.TARGET_NO_EXISTS,"角色不存在");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e) {
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    //授予权限给角色
    @Transactional
    public void endowPrivilegeToRole(Integer roleId, String privilegeIds,Boolean reset) {
        try{
            Role role = roleMapper.selectByPrimaryKey(roleId);
            if(role != null) {
                if(reset)   //重置该角色权限
                {
                    rolePrivilegeMapper.deleteByRoleId(roleId);
                }
                String[] privilegeArr = privilegeIds.split(",");
                for (int i = 0; i < privilegeArr.length; i++) {
                    Privilege privilege = privilegeMapper.selectByPrimaryKey(Integer.parseInt(privilegeArr[i]));
                    if(privilege != null)
                    {
                        doNndowPrivilegeToRole(roleId,privilege);   //递归授权
                    }
                }
                return;
            }
            throw new AppException(Error.TARGET_NO_EXISTS,"角色不存在");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    private void doNndowPrivilegeToRole(Integer roleId,Privilege privilege) {

        RolePrivilegeKey rolePrivilegeKey = new RolePrivilegeKey(roleId, privilege.getId());
        int result = rolePrivilegeMapper.insert(rolePrivilegeKey);

        if(result >= 1)
        {
            List<Privilege> childs = privilegeMapper.getChild(privilege.getId());
            if(childs != null && !childs.isEmpty())
            {
                for(Privilege child : childs)
                {
                    doNndowPrivilegeToRole(roleId,child);
                }
            }
        }
    }

    //从角色收回权限
    @Transactional
    public void takebackPrivilegeFormRole(Integer roleId, String privilegeIds) {
        try{
            Role role = roleMapper.selectByPrimaryKey(roleId);
            if(role != null) {
                String[] privilegeArr = privilegeIds.split(",");
                for (int i = 0; i < privilegeArr.length; i++) {
                    Privilege privilege = privilegeMapper.selectByPrimaryKey(Integer.parseInt(privilegeArr[i]));
                    if(privilege != null)
                    {
                        doTakebackPrivilegeFormRole(roleId,privilege);//递归收回权限
                    }
                }
                return ;
            }
            throw new AppException(Error.TARGET_NO_EXISTS,"角色不存在");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }

    }

    private void doTakebackPrivilegeFormRole(Integer roleId, Privilege privilege) {

        int result = rolePrivilegeMapper.deleteByRoleidAndPrivilegeid(roleId, privilege.getId());
        if(result >= 1)
        {
            List<Privilege> childs = privilegeMapper.getChild(privilege.getId());
            if(childs != null && !childs.isEmpty())
            {
                for(Privilege child : childs)
                {
                    doTakebackPrivilegeFormRole(roleId,child);
                }
            }
        }
    }

    //获取角色下的所有权限
    public List<Privilege> getAllPrivilegeByRoleId(Integer roleId) {
        if(roleId != null)
        {
            return rolePrivilegeMapper.getByRoleId(roleId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public List<Role> getAll()
    {
        return roleMapper.getAll();
    }
}
