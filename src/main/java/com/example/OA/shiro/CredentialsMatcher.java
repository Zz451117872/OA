package com.example.OA.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * Created by aa on 2017/10/30.
 * 证书匹配器，用来验证密码是否正确
 */
public class CredentialsMatcher extends SimpleCredentialsMatcher {


    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info)
    {
        //用户传入的用户名与密码
        UsernamePasswordToken upt = (UsernamePasswordToken)token;
        //用户的密码信息
        String password = (String)info.getCredentials();
        return this.equals(upt.getPassword(),password);
    }
}
