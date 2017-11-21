package com.example.OA.service.manager;

import com.example.OA.dao.RoleMapper;
import com.example.OA.dao.UserMapper;
import com.example.OA.dao.UserRoleMapper;
import com.example.OA.model.Privilege;
import com.example.OA.model.Role;
import com.example.OA.model.User;
import com.example.OA.model.UserRoleKey;
import com.example.OA.model.VO.PrivilegeVO;
import com.example.OA.model.VO.RoleVO;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(this.getClass());

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
                    }
                    return ;
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

    /*
    授予角色给该用户
    userId:用户
    roleIds：授予的角色id串
    reset：是否删除该用户的所有角色再授予
     */
    @Transactional
    public void endowRoleToUser(Integer userId, String roleIds, Boolean reset) {
        try{
            User user = userMapper.selectByPrimaryKey(userId);
            if(user != null) {
                if(reset)    //是否删除该用户的所有角色信息
                {
                    userRoleMapper.deleteByUserId(userId);
                }
                String[] roleIdArr = roleIds.split(",");
                for (int i = 0; i < roleIdArr.length; i++) {
                    Integer roleId = Integer.parseInt(roleIdArr[i]);

                    if(roleMapper.selectByPrimaryKey(roleId) != null) {//判断该角色是否存在
                        UserRoleKey userRoleKey = new UserRoleKey(userId, roleId);

                        if(userRoleMapper.getByUserAndRole(userId,roleId) == null) {//判断该用户是否已拥有该角色
                            userRoleMapper.insert(userRoleKey);
                        }
                    }
                }
                return;
            }
            throw new AppException(Error.TARGET_NO_EXISTS,"用户不存在");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e) {
            e.printStackTrace();
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    //获取 该用户的角色信息，用于前端显示
    public List<RoleVO> getRoleInfoByUser(Integer userId) {
        if(userId != null)
        {
            List<RoleVO> result = Lists.newArrayList();
            List<Integer> roleIds = userRoleMapper.getRoleidByUserid(userId);// 该用户的角色

            List<Role> roles =  roleMapper.getAll(); //所有角色
            if(roles != null && !roles.isEmpty())
               {
                   for(Role role : roles)
                   {
                       if(roleIds != null && !roleIds.isEmpty() && roleIds.contains(role.getId()))   //是我的角色 则为选中状态，反之未选中
                       {
                           result.add(new RoleVO(role,true));
                       }else{               //若是该用户的角色，则标记true,反之标记false
                           result.add(new RoleVO(role,false));
                       }
                   }
                   return result;
               }
            return result;
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    //获取 该用户的权限信息，用于前端显示
    public List<PrivilegeVO> getAllPrivilegeByUserId(Integer userId) {
        if(userId != null)
        {
            List<PrivilegeVO> result = null;
            List<Integer> roleIds = userRoleMapper.getRoleidByUserid(userId);//获取该用户的角色
            if(roleIds != null && !roleIds.isEmpty())
            {
                for(int i=0; i<roleIds.size(); i++)
                {                           //获取角色 对应 的权限信息
                   List<PrivilegeVO> privileges =  roleService.getPrivilegeInfoByRole(roleIds.get(i));
                   if(result == null)
                   {
                       result = privileges;
                   }else{   //若 用户有多个角色，则需要合并 权限信息
                       combine(result,privileges);
                   }
                }
                return result;
            }
            return result;
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    /*
    对多个权限信息集 的选中状态 进行合并
    result：结果集
    privileges:被合并对象
     */
    private void combine(List<PrivilegeVO> result, List<PrivilegeVO> privileges) {

        for(int i=0; i<privileges.size(); i++)
        {
            if(privileges.get(i).getChecked() ) // 被合并对象 若是选中的 则修改 结果集选中状态，反之递归合并子集
            {
                if(!result.get(i).getChecked()) {
                    doCombine(result.get(i), true); // 对权限信息 的选中状态 进行递归修改
                }
            }else{
                List<PrivilegeVO> childs = privileges.get(i).getChilds();
                if(childs != null && !childs.isEmpty())
                {
                    combine(result.get(i).getChilds(),childs);
                }
            }
        }
    }

    /*
    对权限信息 的选中状态 进行递归修改
    privilegeVO：被修改的权限信息
    checked: 是否选中
     */
    private void doCombine(PrivilegeVO privilegeVO, boolean checked) {
        privilegeVO.setChecked(checked);
        List<PrivilegeVO> childs = privilegeVO.getChilds();

        if(childs != null && !childs.isEmpty())
        {
            for(PrivilegeVO child : childs)
            {
                doCombine(child,checked);
            }
        }
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
