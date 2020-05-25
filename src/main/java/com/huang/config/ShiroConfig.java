package com.huang.config;


import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    //第三步：shiroFilterFactoryBean
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("defaultWebSecurityManager") DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //设置安全管理器
        bean.setSecurityManager(defaultWebSecurityManager);

        //添加shiro的内置过滤器
        /*
            anon:无须认证就可以访问
            authc:必须认证了才能访问
            user:必须拥有 记住我 功能才能用
            perms： 拥有对某个资源的权限才能访问
            role:拥有某个角色权限才能访问
        */
        Map<String,String> filterChaindefinitionMap = new LinkedHashMap<>();
        //过滤器规则，从上而下顺序执行，将/**放在最后
        filterChaindefinitionMap.put("/index.html","anon");
        filterChaindefinitionMap.put("/","anon");
        filterChaindefinitionMap.put("/css/**","anon");
        filterChaindefinitionMap.put("/js/**","anon");
        filterChaindefinitionMap.put("/img/**","anon");
        filterChaindefinitionMap.put("/user/login","anon");

        //需要admin角色
        //参数可写多个，表示是某个或某些角色才能通过，多个参数时写 roles["admin，user"]，当有多个参数时必须每个参数都通过才算通过
        filterChaindefinitionMap.put("/emp/**","roles[admin]");
        filterChaindefinitionMap.put("/updateEmp","roles[admin]");
        filterChaindefinitionMap.put("/delete/**","roles[admin]");

        filterChaindefinitionMap.put("/user/logout","logout");
        filterChaindefinitionMap.put("/**","authc");

        bean.setFilterChainDefinitionMap(filterChaindefinitionMap);

        bean.setLoginUrl("/index.html");
        bean.setUnauthorizedUrl("/notRole");
        //bean.setSuccessUrl("/user/login");
        return bean;
    }

    //第二步：DefaultWebSecurityManager
    @Bean(name="defaultWebSecurityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm")UserRealm userRealm){
        DefaultWebSecurityManager defaultWebsecurityManager = new DefaultWebSecurityManager();
        defaultWebsecurityManager.setRealm(userRealm);
        return defaultWebsecurityManager;
    }

    //第一步：创建realm对象，需要自定义
    @Bean
    public UserRealm userRealm(){
        return new UserRealm();
    }
}