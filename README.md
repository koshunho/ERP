# ERP
整合了Redis

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

#### To Do
1.注解方式实现？
