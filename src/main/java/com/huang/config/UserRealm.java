package com.huang.config;

import com.huang.pojo.User;
import com.huang.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;


public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了授权方法:doGetAuthorizationInfo");

        //SimpleAuthorizationInfo
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //拿到当前登录的对象
        Subject subject = SecurityUtils.getSubject();

        //SimpleAuthenticationInfo(Object principal, Object credentials, String realmName)
        //这里就是从认证那里取到当前的对象principal 因为把user作为第一个参数传递过来了
        User currentUser = (User) subject.getPrincipal(); //拿到user对象

        //设置当前用户的权限
        HashSet<String> set = new HashSet<>();
        set.add(currentUser.getRole());
        info.setRoles(set);

        //return info
        return info;
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
        return new SimpleAuthenticationInfo(user,user.getPassword(),"");
    }
}
