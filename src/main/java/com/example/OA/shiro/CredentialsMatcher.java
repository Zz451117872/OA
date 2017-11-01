package com.example.OA.shiro;

import com.example.OA.util.MD5Util;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * Created by aa on 2017/10/30.
 */
public class CredentialsMatcher extends SimpleCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info)
    {
        UsernamePasswordToken upt = (UsernamePasswordToken)token;
        String password = (String)info.getCredentials();
        return this.equals(upt.getPassword(),password);
    }
}
