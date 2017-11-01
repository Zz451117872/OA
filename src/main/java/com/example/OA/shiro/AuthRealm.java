package com.example.OA.shiro;

import com.example.OA.model.User;
import com.example.OA.service.manager.UserService;
import com.example.OA.util.MD5Util;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by aa on 2017/10/30.
 */
public class AuthRealm extends AuthorizingRealm {

    /*
    授权
     */
    @Autowired
    UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        return null;
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
