# ERP
整合了Redis，实现盐加密。 添加了布隆过滤器

### 总结


#### 1. 创建一个redis的配置类RedisConfig

重写了Redis序列化方式，使用Json方式。

当我们的数据存储到Redis的时候，我们的键（key）和值（value）都是通过Spring提供的Serializer序列化到数据库的。

RedisTemplate默认使用的是JdkSerializationRedisSerializer，StringRedisTemplate默认使用的是StringRedisSerializer。

在此我们将自己配置RedisTemplate并定义Serializer。

特别要注意这里的配置，弄了一个下午，就是反序列化的时候一直报错提示实体转换异常com.fasterxml.jackson.databind.exc.MismatchedInputException错误。

```java
@Configuration
public class RedisConfig {

    @Bean("myRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringRedisSerializer);

        template.setHashKeySerializer(stringRedisSerializer);

        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();

        return template;
    }
}
```

#### 2. 创建一个RedisUtil类
直接用RedisTemplate操作Redis，需要很多行代码，因此直接封装好一个RedisUtils，这样写代码更方便点。这个RedisUtils交给Spring容器实例化，使用时直接注解注入。

```java
@Component
@SuppressWarnings("all")
public final class RedisUtil {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true / false
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //省略.............................

}
```

#### 3.手动在service层调用
在service层实现redis，先考虑缓存，再考虑DB。

可以自己设定key的值，可以没有任何规则可言，自己设定生命周期。

```java
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public List<Employee> selectAllEmployee() {
        String key = "allEmployee";

        boolean hasKey = redisUtil.hasKey(key);

        if(hasKey){
            List<Employee> list = (List<Employee>) redisUtil.get(key);
            System.out.println("从redis中获取了allEmployee");
            return list;
        }
        List<Employee> list = employeeMapper.selectAllEmployee();
        redisUtil.set(key, list, 500);
        System.out.println("从db中获取allEmployee");
        return list;
    }
    //省略...................
}
```

#### 4.注解方式实现
1.在配置类上加上@EnableCaching

当你在配置类(@Configuration)上使用@EnableCaching注解时，会触发一个post processor。

这会扫描每一个spring bean，查看是否已经存在注解对应的缓存。如果找到了，就会自动创建一个代理拦截方法调用，使用缓存的bean执行处理。

2.在Service层加上@CacheConfig
```java
@Service
@CacheConfig(cacheNames = "departmentCache") // 本类内方法指定使用缓存时，默认的名称就是departmentCache
public class DepartmentServiceImpl implements DepartmentService{

    @Autowired
    private DepartmentMapper departmentMapper;


    @Override
    @Cacheable
    public List<Department> selectAllDepartment() {
        System.out.println("selectAllDepartment() did not hit cache!");
        return departmentMapper.selectAllDepartment();
    }

    @Override
    @Cacheable
    public Department selectDepartmentById(int id) {
        System.out.println("selectDepartmentById did not hit cache!");
        return departmentMapper.selectDepartmentById(id);
    }
}
```
对数据库操作的函数按功能加上 @CachePut @CacheEvict @Cacheable

@CachePut 是将数据加入到redis缓存中

@Cacheable 在获取数据的时候会先查询缓存，如果缓存中存在，则不执行查询数据库的方法，如果不存在则查询数据库，并加入到缓存中。

@CacheEvict 一般注解到删除数据的操作上，会将一条或多条数据从缓存中删除。

#### To do
shiro + Redis，有点难搞，不太整得明白

-------
#### 盐加密

之前一直欠着没弄，今天来补作业了。

首先是在UserRealm.java中设置加盐
```java
package com.huang.config;

public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //省略....
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
        // 因为上面的授权需要user对象，这里就直接传了User
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
```

然后再在ShiroConfig.java进行配置 密码校验规则HashedCredentialsMatcher
```java
package com.huang.config;


import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
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
        //省略....
    }

    //第二步：DefaultWebSecurityManager
    @Bean(name="defaultWebSecurityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm")UserRealm userRealm){
        //省略....
    }

    //第一步：创建realm对象，需要自定义
    @Bean
    public UserRealm userRealm(@Qualifier("hashedCredentialsMatcher") HashedCredentialsMatcher matcher){
        //省略....
    }

    /**
     * 密码校验规则HashedCredentialsMatcher
     * 这个类是为了对密码进行编码的 ,
     * 防止密码在数据库里明码保存 , 当然在登陆认证的时候 ,
     * 这个类也负责对form里输入的密码进行编码
     * 处理认证匹配处理器：如果自定义需要实现继承HashedCredentialsMatcher
     */
    @Bean("hashedCredentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        //指定加密方式为MD5
        credentialsMatcher.setHashAlgorithmName("MD5");
        //加密次数
        credentialsMatcher.setHashIterations(1024);
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        return credentialsMatcher;
    }
}
```
把这个HashedCredentialsMatcher注入到realm对象中就可以。

补上一个明文转密的代码
```java
public class MD5_generator {

    @Test
    public static void main(String[] args) {
        String hashAlgorithName = "MD5";
        String password = "123";
        int hashIterations = 1024;//加密次数
        ByteSource credentialsSalt = ByteSource.Util.bytes("nmsl");
        Object obj = new SimpleHash(hashAlgorithName, password, credentialsSalt, hashIterations);
        System.out.println(obj);
    }
}
```
-------
#### 布隆过滤器
原理就不多说了，直接看怎么配置。

由于不是数学家（笑い），所以直接使用了咕噜的guava包。
```xml
        <!-- https://mvnrepository.com/artifact/com.google.guava/guava -->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>29.0-jre</version>
        </dependency>
```

##### 1.预热
当Spring启动后，就初始化布隆过滤器。它需要从数据库中先加载所有的用户ID
```java
@Component
public class BloomFilterInit implements ApplicationRunner {

    private BloomFilter bloomFilter;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Integer> list = employeeMapper.getAllEmployeesId();
        if(list.size()>0){
            bloomFilter=bloomFilter.create(Funnels.integerFunnel(),list.size(),0.01);
            for(int i = 0; i < list.size(); i++){
                bloomFilter.put(list.get(i));
            }
            System.out.println("预热employeesId到布隆过滤器成功！");
        }
    }

    public BloomFilter getIntegerBloomFilter(){
        return bloomFilter;
    }
}
```

##### 2.在Service中调用
```java
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Resource
    private RedisUtil redisUtil;

    @Autowired
    private BloomFilterInit bloomFilterInit;
    
    //省略。。。。。。。。

    @Override
    public Employee selectEmployeeById(int id) {
        String key = "Employee" + id;

        if(!bloomFilterInit.getIntegerBloomFilter().mightContain(id)){
            System.out.println("该EmployeeId在布隆过滤器中不存在！");
            return null;
        }
        Employee redisEmployee = (Employee) redisUtil.get(key);
        if(redisEmployee!=null){
            System.out.println("从redis中返回数据！");
            return redisEmployee;
        }
        System.out.println("从DB中返回数据！");
        Employee dbEmployee = employeeMapper.selectEmployeeById(id);
        if(dbEmployee!=null){
            System.out.println("将数据缓存到Redis中！");
            redisUtil.set(key, dbEmployee);
        }
        return dbEmployee;
    }


    @Override
    public int addEmployee(Employee employee) {
        int ret = employeeMapper.addEmployee(employee);

        redisUtil.del("allEmployee");
        System.out.println("新增:从redis中删除了全部的Employee");

        bloomFilterInit.getIntegerBloomFilter().put(employee.getId());  //注意这里
        return ret;
    }
}
```

##### 注意点
1. 在Service层addEmployee的时候，这时候的对象是没有ID的，因为存入数据库的时候ID自增。
    
    而我这时候需要把当前对象的ID设置进布隆过滤器。原本是像再从数据库中取出这个对象，想了想感觉就是脱裤子放屁了。
    
    解决办法：在mapper.xml中
    ```xml
    <!--需求：使用MyBatis往MySQL数据库中插入一条记录后，需要返回该条记录的自增主键值。-->
    <!--“useGeneratedKeys”和“keyProperty”必须添加，而且keyProperty一定得和java对象的属性名称一直，而不是表格的字段名-->
    <insert id="addEmployee" useGeneratedKeys="true" keyProperty="id" parameterType="employee">
        insert into springboot.employee (lastName,email,gender,departmentId,birth)
        values (#{lastName},#{email},#{gender},#{department.id},#{birth})
    </insert>
    ```