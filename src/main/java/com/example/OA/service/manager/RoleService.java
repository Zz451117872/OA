package com.example.OA.service.manager;

import com.example.OA.dao.*;
import com.example.OA.model.Privilege;
import com.example.OA.model.Role;
import com.example.OA.model.RolePrivilegeKey;
import com.example.OA.model.VO.PrivilegeVO;
import com.example.OA.model.VO.RoleVO;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
public class RoleService {

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    UserRoleMapper userRoleMapper;

    @Autowired
    RolePrivilegeMapper rolePrivilegeMapper;

    @Autowired
    PrivilegeMapper privilegeMapper;

    @Autowired
    PrivilegeService privilegeService;

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
                Role temp = roleMapper.getByRolename(role.getRoleName());
                if(temp == null || temp.getRoleName().equals(role.getRoleName())) {
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
            e.printStackTrace();
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    /*
    授予权限给角色
    roleId：授予哪个角色
    privilegeIds：授予哪些权限
    reset：授予前是否删除该角色的所有权限信息
     */
    @Transactional
    public void endowPrivilegeToRole(Integer roleId, String privilegeIds,Boolean reset) {
        try{
            Role role = roleMapper.selectByPrimaryKey(roleId);
            if(role != null) {
                if(reset)   //删除该角色权限
                {
                    rolePrivilegeMapper.deleteByRoleId(roleId);
                }
                if(privilegeIds == null || "".equals(privilegeIds))
                {
                    return;
                }
                String[] privilegeArr = privilegeIds.split(",");
                for (int i = 0; i < privilegeArr.length; i++) {

                    Integer privilegeId = Integer.parseInt(privilegeArr[i]);
                    if(privilegeMapper.selectByPrimaryKey(privilegeId) != null)//判断该权限是否存在
                    {
                        doEndowPrivilegeToRole(roleId,privilegeId);   //递归授权
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
            e.printStackTrace();
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    /*
    对 角色 授予权限
     */
    @Transactional
    private void doEndowPrivilegeToRole(Integer roleId,Integer privilegeId) {

        RolePrivilegeKey rolePrivilegeKey = new RolePrivilegeKey(roleId, privilegeId);
                                                    //判断该角色是否已拥有该权限
        if(rolePrivilegeMapper.getByRoleAndPrivilege(roleId,privilegeId) == null) {

            int result = rolePrivilegeMapper.insert(rolePrivilegeKey);
            if (result >= 1) {                                  //递归授予子权限
                List<Privilege> childs = privilegeMapper.getChild(privilegeId);
                if (childs != null && !childs.isEmpty()) {
                    for (Privilege child : childs) {
                        doEndowPrivilegeToRole(roleId, child.getId());
                    }
                }
            }
        }
    }


    //获取 该 角色 的权限信息,用于前台展示，包含不拥有的权限
    public List<PrivilegeVO> getPrivilegeInfoByRole(Integer roleId) {
        if(roleId != null)
        {
            List<PrivilegeVO> result = Lists.newArrayList();
                            //该角色 拥有 的权限
            List<Integer> privilegeIds =  rolePrivilegeMapper.getPrivilegeIdByRoleid(roleId); //获取该角色拥有的权限
                            //所有顶级权限
            List<Privilege> privileges = privilegeService.getTopPrivilege();
            for(Privilege privilege : privileges)
            {
                result.add(convertPrivilegeVO(privilege,privilegeIds));
            }
            return result;
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    /*
    判断 该 角色是否拥有 目标权限，对拥有的权限加选中标记，反之。。
    privilege：目标权限
    privilegeIds：角色拥有的权限id
     */
    private PrivilegeVO convertPrivilegeVO(Privilege privilege, List<Integer> privilegeIds) {
                                    //获取父权限名
        Privilege parent = privilegeMapper.selectByPrimaryKey(privilege.getParent());
        String parentName = parent == null ? "无" : parent.getPrivilegeName();

        PrivilegeVO result = null;
        if(privilegeIds != null && !privilegeIds.isEmpty() && privilegeIds.contains(privilege.getId()))
        {
            result = new PrivilegeVO(privilege,parentName,true);
        }else{              // 对 角色 拥有的权限 标记为 true，反之 标记为false;
            result = new PrivilegeVO(privilege,parentName,false);
        }
                //若目标权限 有子权限，则需要递归判断
        List<Privilege> childs = privilege.getChilds();
        if(childs != null && !childs.isEmpty())
        {
            List<PrivilegeVO> privilegeVOs = Lists.newArrayList();
            for(Privilege child : childs)
            {
                privilegeVOs.add(convertPrivilegeVO(child,privilegeIds));//对子权限进行递归判断
            }
            result.setChilds(privilegeVOs);
        }else {
            result.setChilds(null);
        }
        return result;
    }


    //获取属于该角色的权限，不包含不拥有的权限
    public List<Privilege> getPrivilegeByRole(Integer roleId)
    {
        if(roleId != null)
        {
            List<Privilege> result = Lists.newArrayList();
                                    //获取该角色拥有的权限
            List<Integer> privilegeIds =  rolePrivilegeMapper.getPrivilegeIdByRoleid(roleId);
            if(privilegeIds != null && !privilegeIds.isEmpty())
            {                                  //获取顶级权限
                List<Privilege> topPrivileges = privilegeService.getTopPrivilege();
                filtrateMyPrivilege(topPrivileges,privilegeIds,result);//筛选出属于该角色的权限
            }
            return result;
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    /*
    从目标权限集中 筛选出 角色 拥有的权限
    topPrivileges：目标权限集
    privilegeIds：角色拥有的权限id
    result：返回结果
     */
    private void filtrateMyPrivilege(List<Privilege> topPrivileges, List<Integer> privilegeIds, List<Privilege> result) {
        if(topPrivileges != null && privilegeIds != null)
        {
            for (Privilege privilege : topPrivileges)
            {
                if(privilegeIds.contains(privilege.getId()))
                {
                    result.add(privilege);
                }else {
                    List<Privilege> childs = privilege.getChilds();
                    if(childs != null && !childs.isEmpty())
                    {
                        filtrateMyPrivilege(childs,privilegeIds,result);//对子权限递归筛选
                    }
                }
            }
        }
        return;
    }

    public PageInfo getAll(Integer pageNum,Integer pageSize)
    {
        try{
            PageHelper.startPage(pageNum,pageSize);
            List<Role>  roles = roleMapper.getAll();
            List<RoleVO> roleVOs = convertRoleVOs(roles);
            PageInfo pageInfo = new PageInfo(roles);
            pageInfo.setList(roleVOs);
            return pageInfo;
        }catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    private List<RoleVO> convertRoleVOs(List<Role> roles) {
        try{
            if(roles != null && !roles.isEmpty())
            {
                List<RoleVO> result = Lists.newArrayList();
                for(Role role : roles)
                {
                    result.add(convertVO(role));
                }
                return result;
            }
            return null;
        }catch (Exception e)
        {
            throw e;
        }
    }

    private RoleVO convertVO(Role role) {
        try{
            if(role != null)
            {
                RoleVO roleVO = new RoleVO();
                roleVO.setId(role.getId());
                roleVO.setCreateTime(role.getCreateTime());
                roleVO.setUpdateTime(role.getUpdateTime());
                roleVO.setDescription(role.getDescription());
                roleVO.setRoleName(role.getRoleName());
                return roleVO;
            }
            return null;
        }catch (Exception e)
        {
            throw e;
        }
    }
}
