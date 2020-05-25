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
        //配置匿名可访问页面和静态文件
        filterChaindefinitionMap.put("/index.html","anon");
        filterChaindefinitionMap.put("/","anon");
        filterChaindefinitionMap.put("/css/**","anon");
        filterChaindefinitionMap.put("/js/**","anon");
        filterChaindefinitionMap.put("/img/**","anon");
        filterChaindefinitionMap.put("/user/login","anon");
        filterChaindefinitionMap.put("/user/logout","logout");
        filterChaindefinitionMap.put("/**","authc");

        bean.setFilterChainDefinitionMap(filterChaindefinitionMap);

        bean.setLoginUrl("/index.html");
        //bean.setSuccessUrl("/user/login");
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
