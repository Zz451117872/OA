package com.example.OA.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by aa on 2017/10/30.
 */
public class CredentialsMatcher extends SimpleCredentialsMatcher {


    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info)
    {
        UsernamePasswordToken upt = (UsernamePasswordToken)token;
        String password = (String)info.getCredentials();
        System.out.println(new String(upt.getPassword())+"    "+password);
        return this.equals(upt.getPassword(),password);
    }
}
