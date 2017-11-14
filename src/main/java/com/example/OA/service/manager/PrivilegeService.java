package com.example.OA.service.manager;

import com.example.OA.dao.PrivilegeMapper;
import com.example.OA.dao.RolePrivilegeMapper;
import com.example.OA.model.Privilege;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by aa on 2017/10/31.
 */
@Service
public class PrivilegeService {

    @Autowired
    PrivilegeMapper privilegeMapper;

    @Autowired
    RolePrivilegeMapper rolePrivilegeMapper;

    public String add(Privilege privilege) {
        if(privilege != null)
        {
            String url = privilege.getUrl();
            if(StringUtils.isNotBlank(url) && privilegeMapper.getByUrl(url) == null)
            {
                privilege.setCreateTime(new Date());
                privilegeMapper.insert(privilege);
                return url;
            }
            throw new AppException(Error.TARGET_EXISTSED,"privilege existed");
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public String update(Privilege privilege) {
        if(privilege != null)
        {
            String url = privilege.getUrl();
            if(StringUtils.isNotBlank(url) && privilegeMapper.getByUrl(url) == null)
            {
                privilege.setUpdateTime(new Date());
                privilegeMapper.updateByPrimaryKeySelective(privilege);
                return url;
            }
            throw new AppException(Error.TARGET_EXISTSED,"privilege existed");
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public String deleteById(Integer privilegeId) {
        //删除权限时，要级联删除 角色与权限的对应关系
        if(privilegeId != null)
        {
            if(privilegeMapper.selectByPrimaryKey(privilegeId) != null)
            {
               int result = privilegeMapper.deleteByPrimaryKey(privilegeId);
                if(result >= 1)
                {
                    rolePrivilegeMapper.deleteByPrivilegeId(privilegeId);
                    return "success";
                }
                throw new AppException(Error.UNKNOW_EXCEPTION,"delete faild");
            }
            throw new AppException(Error.TARGET_NO_EXISTS,"privilege not existed");
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }
}
