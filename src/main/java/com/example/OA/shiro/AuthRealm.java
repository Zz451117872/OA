package com.example.OA.shiro;

import com.example.OA.dao.PrivilegeMapper;
import com.example.OA.dao.RoleMapper;
import com.example.OA.dao.RolePrivilegeMapper;
import com.example.OA.dao.UserRoleMapper;
import com.example.OA.model.Privilege;
import com.example.OA.model.Role;
import com.example.OA.model.User;
import com.example.OA.service.manager.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by aa on 2017/10/30.
 */
public class AuthRealm extends AuthorizingRealm {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    UserRoleMapper userRoleMapper;

    @Autowired
    PrivilegeMapper privilegeMapper;

    @Autowired
    RolePrivilegeMapper rolePrivilegeMapper;

    /*
    授权
     */

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        User user =  (User)principalCollection.getPrimaryPrincipal();

        System.out.println("user:"+user.getId());

        //把principals放session中 key=userId value=principals
        SecurityUtils.getSubject().getSession().setAttribute(String.valueOf(user.getId()), SecurityUtils.getSubject().getPrincipals());

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //赋予角色
        List<Integer> roleIds = userRoleMapper.getRoleidByUserid(user.getId());
        if(roleIds != null && !roleIds.isEmpty())
        {
            List<Role> roles = roleMapper.getByRoleIds(roleIds);
            if(roles != null && !roles.isEmpty())
            {
                for(Role role : roles)
                {
                    info.addRole(role.getRoleName());
                    logger.info("addRole:"+role.getRoleName());

                    List<Integer> privilegeIds = rolePrivilegeMapper.getPrivilegeIdByRoleid(role.getId());
                    if(privilegeIds != null && !privilegeIds.isEmpty())
                    {
                        List<Privilege> privileges = privilegeMapper.getByIds(privilegeIds);
                        //赋予权限
                        if(privileges != null && !privileges.isEmpty())
                        {
                            for(Privilege privilege : privileges){
                                initPrivilegeToShiro(privilege,info);
                            }
                        }
                    }else {
                        logger.info("角色 "+role.getRoleName()+" 没有权限");
                    }
                }
            }
        }else {
            logger.info("用户 "+user.getUsername()+" 没有角色");
        }
        return info;
    }

    private void initPrivilegeToShiro(Privilege privilege, SimpleAuthorizationInfo info) {

        info.addStringPermission(privilege.getUrl());
        logger.info("addStringPermission:"+privilege.getUrl());

        List<Privilege> childs = privilegeMapper.getChild(privilege.getId());
        if(childs != null && !childs.isEmpty())
        {
            for(Privilege child : childs)
            {
                initPrivilegeToShiro(child,info);
            }
        }
    }

    /*
    认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        UsernamePasswordToken token = (UsernamePasswordToken)authenticationToken;
        String username = token.getUsername();
        User user = userService.getByUsername(username);
        return new SimpleAuthenticationInfo(user,user.getPassword(),this.getClass().getName());
    }
}
