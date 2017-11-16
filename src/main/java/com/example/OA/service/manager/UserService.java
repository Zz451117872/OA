package com.example.OA.service.manager;

import com.example.OA.dao.RoleMapper;
import com.example.OA.dao.UserMapper;
import com.example.OA.dao.UserRoleMapper;
import com.example.OA.model.Privilege;
import com.example.OA.model.Role;
import com.example.OA.model.User;
import com.example.OA.model.UserRoleKey;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.google.common.collect.Lists;
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
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserRoleMapper userRoleMapper;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    RoleService roleService;

    //添加一个用户
    public void addUser(User user) {
        try{
            String username = user.getUsername(); //用户名唯一
            if(userMapper.getByUsername(username) == null)
            {
                user.setCreateTime(new Date());
                 userMapper.insert(user);
                 return ;
             }
            throw new AppException(Error.TARGET_EXISTSED,"用户名已存在");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e) {
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    //更新一个用户
    public void updateUser(User user) {
        try{
            user.setUsername(null); //用户名不能修改
            user.setCreateTime(null);
            user.setUpdateTime(new Date());
            userMapper.updateByPrimaryKeySelective(user);
            return ;
        }catch (Exception e) {
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    //删除一个用户
    @Transactional
    public void deleteUser(Integer userId) {
        //删除用户时，用户与角色的对应关系也要删除
        try{
            if(userMapper.selectByPrimaryKey(userId) != null)
            {
                int result = userMapper.deleteByPrimaryKey(userId);
                if(result >= 1)
                {
                   List<Integer> roleIds =  userRoleMapper.getRoleidByUserid(userId);
                    if(roleIds != null && !roleIds.isEmpty())
                    {
                        userRoleMapper.deleteByRoleidsAndUserid(userId,roleIds);
                        return ;
                    }
                }
                throw new AppException(Error.UNKNOW_EXCEPTION,"database inner error");
            }
            throw new AppException(Error.TARGET_NO_EXISTS,"用户不存在");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e) {
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    //授予角色给该用户
    @Transactional
    public void endowRoleToUser(Integer userId, String roleIds) {
        try{
            User user = userMapper.selectByPrimaryKey(userId);
            if(user != null) {
                String[] roleIdArr = roleIds.split(",");
                for (int i = 0; i < roleIdArr.length; i++) {
                    Integer roleId = Integer.parseInt(roleIdArr[i]);

                    if(roleMapper.selectByPrimaryKey(roleId) != null) {
                        UserRoleKey userRoleKey = new UserRoleKey(userId, roleId);
                        userRoleMapper.insert(userRoleKey);
                    }
                }
                return;
            }
            throw new AppException(Error.TARGET_NO_EXISTS,"用户不存在");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e) {
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    //从该用户收回角色
    @Transactional
    public void takebackRoleFromUser(Integer userId, String roleIds) {
       try{
            User user = userMapper.selectByPrimaryKey(userId);
            if(user != null) {
                String[] roleIdArr = roleIds.split(",");
                for (int i = 0; i < roleIdArr.length; i++) {

                    Integer roleId = Integer.parseInt(roleIdArr[i]);
                    userRoleMapper.deleteByRoleidAndUserid(userId, roleId);
                }
                return;
            }
           throw new AppException(Error.TARGET_NO_EXISTS,"用户不存在");
        }catch (AppException e)
       {
           throw e;
       }catch (Exception e) {
           throw new AppException(Error.UNKNOW_EXCEPTION);
       }
    }

    public List<Role> getAllRoleByUserId(Integer userId) {
        if(userId != null)
        {
            List<Integer> roleIds = userRoleMapper.getRoleidByUserid(userId);

            if(roleIds != null && !roleIds.isEmpty())
            {
                return roleMapper.getByRoleIds(roleIds);
            }
            return null;
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public List<Privilege> getAllPrivilegeByUserId(Integer userId) {
        if(userId != null)
        {
            List<Privilege> result = Lists.newArrayList();
            List<Role> roles = getAllRoleByUserId(userId);
            if(roles != null && !roles.isEmpty())
            {
                for(int i=0; i<roles.size(); i++)
                {
                   List<Privilege> privileges =  roleService.getAllPrivilegeByRoleId(roles.get(i).getId());
                    if(privileges != null && !privileges.isEmpty())
                    {
                        result.addAll(privileges);
                    }
                }
                return result;
            }
            return null;
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public User getByIdOrUsername(Integer userId, String username) {
        if(userId != null || username != null)
        {
            return userMapper.getByIdOrUsername(userId,username);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public User getByUsername(String username) {
        if(username != null)
        {
            return userMapper.getByUsername(username);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public List<User> getAll()
    {
        return userMapper.getAll();
    }
}
