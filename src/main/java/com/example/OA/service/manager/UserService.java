package com.example.OA.service.manager;

import com.example.OA.mapper.RoleMapper;
import com.example.OA.mapper.UserMapper;
import com.example.OA.mapper.UserRoleMapper;
import com.example.OA.model.Privilege;
import com.example.OA.model.Role;
import com.example.OA.model.User;
import com.example.OA.model.UserRoleKey;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.google.common.collect.Lists;
import com.sun.tracing.dtrace.Attributes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public String addUser(User user) {
        if(user != null)
        {
            String username = user.getUsername();
          if(StringUtils.isNotBlank(username) && userMapper.getByUsername(username) != null)
          {
              user.setCreateTime(new Date());
              userMapper.insert(user);
              return username;
          }else{
              throw new AppException(Error.EXISTSED);
          }
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public String updateUser(User user) {
        if(user != null)
        {
            user.setUsername(null);
            user.setCreateTime(null);
            user.setUpdateTime(new Date());
            userMapper.updateByPrimaryKeySelective(user);
            return user.getId()+"";
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public String deleteUser(Integer userId) {
        if(userId != null)
        {
            if(userMapper.selectByPrimaryKey(userId) != null)
            {
                userMapper.deleteByPrimaryKey(userId);
                return "success";
            }
            throw new AppException(Error.NO_EXISTS);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public int endowRoleToUser(Integer userId, String roleIds) {
        if(userId != null && StringUtils.isNotBlank(roleIds))
        {
            User user = userMapper.selectByPrimaryKey(userId);
            if(user == null)
            {
                throw new AppException(Error.NO_EXISTS,"user no exist");
            }
            int result = 0;
            String[] roleIdArr = roleIds.split(",");
            for(int i=0; i<roleIdArr.length; i++)
            {
                Integer roleId = Integer.parseInt(roleIdArr[i]);
                if(roleMapper.selectByPrimaryKey(roleId) != null)
                {
                    UserRoleKey userRoleKey = new UserRoleKey(userId,roleId);
                    userRoleMapper.insert(userRoleKey);
                    result++;
                }
            }
            return result;
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public int takebackRoleFromUser(Integer userId, String roleIds) {
        if(userId != null && StringUtils.isNotBlank(roleIds))
        {
            User user = userMapper.selectByPrimaryKey(userId);
            if(user == null)
            {
                throw new AppException(Error.NO_EXISTS,"user no exist");
            }
            int result = 0;
            String[] roleIdArr = roleIds.split(",");
            for(int i=0; i<roleIdArr.length; i++)
            {
                Integer roleId = Integer.parseInt(roleIdArr[i]);
                if(roleMapper.selectByPrimaryKey(roleId) != null)
                {
                    userRoleMapper.deleteByRoleidAndUserid(userId,roleId);
                    result++;
                }
            }
            return result;
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
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
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }
}
