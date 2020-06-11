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
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.HashSet;



public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //System.out.println("执行了授权方法:doGetAuthorizationInfo");

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
        //System.out.println("执行了认证方法:AuthenticationInfo");
        //用户名，密码 从数据库中取

        //userToken从LoginController中取
        // UsernamePasswordToken对象用来存放提交的登录信息
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;
        //仅根据用户名取用户
        User user = userService.selectUserByShiro(userToken.getUsername());

        if(user == null){
            return null;
        }
        //shiro帮做密码认证

        // 根据用户的情况，来构建AuthenticationInfo对象并返回，通常使用的实现类为SimpleAuthenticationInfo
        // 以下信息从数据库中获取
        // （1）principal：认证的实体信息，可以是email，也可以是数据表对应的用户的实体类对象
        Object principal = user;
        // （2）credentials：密码
        Object credentials = user.getPassword();
        // （3）realmName：当前realm对象的name，调用父类的getName()方法即可
        String realmName = getName();
        // （4）盐值：取用户信息中唯一的字段来生成盐值，避免由于两个用户原始密码相同，加密后的密码也相同
        ByteSource credentialsSalt = ByteSource.Util.bytes(user.getUsername());
        return new SimpleAuthenticationInfo(principal, credentials, credentialsSalt,
                realmName);
    }
}
