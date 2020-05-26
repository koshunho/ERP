# ERP
删除了之前的拦截器，整合了shiro。

### 总结
创建一个shiro的配置类ShiroConfig

#### 第一步：创建Realm对象，需要自定义
```Java
    @Bean
    public UserRealm userRealm(){
        return new UserRealm();
    }
```
用户的授权和认证就在UserRealm里
```Java
public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {  
        return null;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        return null;
    }
}
```
#### 第二步：DefaultWebSecurityManager
```java
    @Bean(name="defaultWebSecurityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm")UserRealm userRealm){
        DefaultWebSecurityManager defaultWebsecurityManager = new DefaultWebSecurityManager();
        defaultWebsecurityManager.setRealm(userRealm);
        return defaultWebsecurityManager;
    }
```

#### 第三步：ShiroFilterFactoryBean
```java
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
    
        return bean;
    }
```

### 在LoginController中就用token来验证用户了
```java
    @RequestMapping("/user/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, Model model, HttpSession session){
        //User user = userService.selectUserByName(username, password);

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try{
            subject.login(token);
            session.setAttribute("loginUser",username);
            return "redirect:/main.html";
        }catch (UnknownAccountException e){
            model.addAttribute("msg","用户名错误！");
            return "index";
        }catch(IncorrectCredentialsException e){
            model.addAttribute("msg","密码错误！");
            return "index";
        }
```
token是相当于全局的，都能取得到。（？）
### 认证

#### 在UserRealm重写doGetAuthenticationInfo()方法
```java
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
        return new SimpleAuthenticationInfo(user,user.getPassword(),""); //注意第一个参数传入了当前的user对象
    }
```

### 授权

#### 在UserRealm重写doGetAuthorizationInfo()方法
```java
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
```
能取到currentUser对象是因为在认证时 
```java
return new SimpleAuthenticationInfo(user,user.getPassword(),"");
```
把当前对象作为第一个参数传递了

在使用授权之前，在数据库中增加一个字段role。对应的POJO的User类也必须要加上role属性。
<div align=center><img src="https://s1.ax1x.com/2020/05/26/tPF5FK.png"/></div>

因为在后面才想到集成shiro，在写Controller的时候URL就乱写了好多不方便归类，然后前端又按照这样传值取值了不太好改，restful应该统一 /user/** ，/admin/** 的。

这里有个注意点：anon, authc, authcBasic, user 是第一组认证过滤器，perms, port, rest, roles, ssl 是第二组授权过滤器，要通过授权过滤器，就先要完成登陆认证操作（即先要完成认证才能前去寻找授权) 才能走第二组授权器。


其中 doGetAuthorizationInfo() 只有在需要权限认证时才会进去，比如前面配置类中配置了管理员角色，这时进入 /admin 时就会进入 doGetAuthorizationInfo 方法来检查权限。
```java
filterChaindefinitionMap.put("/emp/**","roles[admin]");
```

其他就常规操作了。另外设置无权限时跳转的 url： bean.setUnauthorizedUrl("/notRole");
```java
//在LoginController中
    @RequestMapping("/notRole")
    public String unauthorized(){
        return "error/401";
    }
```

实际上就是个401错误。

来测试一下，数据库的user中用户名为nmsl的角色role = user，用它测一下权限。

登录之后能看用户列表。
<div align=center><img src="https://s1.ax1x.com/2020/05/26/tPk8Tx.png"/></div>

但是点击 添加、编辑、删除 的话。。。就弹出自己设置的页面
<div align=center><img src="https://s1.ax1x.com/2020/05/26/tPkJk6.png"/></div>

### Shiro整合thymeleaf
要是身份是user的话，添加、编辑、删除 按钮就根本不应该出现

1.导入一个thymeleaf-extras-shiro包。

2.在ShiroConfig中配置
```java
    //ShiroDialect：用来整合shiro 和 thymeleaf
    @Bean
    public ShiroDialect shiroDialect(){
        return new ShiroDialect();
    }
```

3.修改网页
首先导入命名空间xmlns:shiro="http://www.pollix.at/thymeleaf/shiro"
```html
<a shiro:hasRole="admin" class="btn btn-sm btn-primary" th:href="@{'/emp/'+${emp.id}}">编辑</a>
<a shiro:hasRole="admin" class="btn btn-sm btn-danger" th:href="@{'/delete/'+${emp.id}}">删除</a>
```
这里保留一个添加按钮，用来测试401页面。

4.效果
<div align=center><img src="https://s1.ax1x.com/2020/05/26/ti7Fds.png"/></div>

### To Do
1.认证的MD5的盐值加密

