package com.huang.config;

import com.huang.pojo.User;
import com.huang.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;


public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了授权方法:doGetAuthorizationInfo");
        return null;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("执行了认证方法:AuthenticationInfo");
        //用户名，密码 从数据库中取
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;
        //仅根据用户名取用户
        User user = userService.selectUserByShiro(userToken.getUsername());

        if(user == null){
            return null;
        }
        //shiro帮做密码认证
        return new SimpleAuthenticationInfo("",user.getPassword(),"");
    }
}
