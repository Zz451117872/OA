package com.example.OA.service.manager;

import com.example.OA.dao.PrivilegeMapper;
import com.example.OA.dao.RolePrivilegeMapper;
import com.example.OA.model.Privilege;
import com.example.OA.model.VO.PrivilegeVO;
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
public class PrivilegeService {

    @Autowired
    PrivilegeMapper privilegeMapper;

    @Autowired
    RolePrivilegeMapper rolePrivilegeMapper;

    //获取 顶级权限，并填充子权限
    public List<Privilege> getTopPrivilege()
    {
        try{
            List<Privilege> result = privilegeMapper.getTopPrivilege();//得到顶级权限
            if(result != null)
            {
                for(Privilege privilege : result)
                {
                    fillChildToParent(privilege);   //递归填充子权限
                }
                return result;
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"权限数据未初始化");
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    //递归 填充子权限
    private void fillChildToParent(Privilege privilege) {
        if(privilege != null)
        {
            List<Privilege> childs = privilegeMapper.getChild(privilege.getId());
            privilege.setChilds(childs);
            if(childs != null && !childs.isEmpty())
            {
                for (Privilege child : childs)
                {
                    fillChildToParent(child);
                }
                return;
            }
            return;
        }
        return;
    }


    //添加一个权限
    public String add(Privilege privilege) {
        if(privilege != null)
        {
            String url = privilege.getUrl();    //权限url唯一
            if(privilegeMapper.getByUrl(url) == null)
            {
                privilege.setCreateTime(new Date());
                privilegeMapper.insert(privilege);
                return url;
            }
            throw new AppException(Error.TARGET_EXISTSED,"该权限已存在");
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //更新一个权限
    public String update(Privilege privilege) {
        if(privilege != null)
        {
            if(privilegeMapper.selectByPrimaryKey(privilege.getId()) != null) {
                String url = privilege.getUrl(); //权限url唯一
                if (privilegeMapper.getByUrlEorId(url,privilege.getId()) == null) {
                    privilege.setUpdateTime(new Date());
                    privilegeMapper.updateByPrimaryKeySelective(privilege);
                    return url;
                }
                throw new AppException(Error.TARGET_EXISTSED, "权限url已存在");
            }
            throw new AppException(Error.TARGET_NO_EXISTS,"权限不存在");
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //删除一个权限
    @Transactional
    public void deleteById(Integer privilegeId) {
        //删除权限时，要级联删除 角色与权限的对应关系
        if(privilegeId != null)
        {
            if(privilegeMapper.selectByPrimaryKey(privilegeId) != null)
            {
               int result = privilegeMapper.deleteByPrimaryKey(privilegeId);
                if(result >= 1)
                {
                    rolePrivilegeMapper.deleteByPrivilegeId(privilegeId);
                    return ;
                }
                throw new AppException(Error.UNKNOW_EXCEPTION,"database inner error");
            }
            throw new AppException(Error.TARGET_NO_EXISTS,"权限不存在");
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public List<Privilege> getChild(Integer parentId) {
        return privilegeMapper.getChild(parentId);
    }
}
