package com.huang;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.huang.pojo.Department;
import com.huang.pojo.Employee;
import com.huang.pojo.User;
import com.huang.service.DepartmentService;
import com.huang.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;


@SpringBootTest
public class RedisTest {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DepartmentService departmentService;

    @Test
    public void test1() throws JsonProcessingException {
        User user = new User(1, "小黄", "123456", "老板");
        //String jsonUser = new ObjectMapper().writeValueAsString(user);
        redisTemplate.opsForValue().set("user",user);
        System.out.println(redisTemplate.opsForValue().get("user"));
    }

    @Test
    public void test2(){
        Department department = departmentService.selectDepartmentById(1);
        Employee employee = new Employee();
        //employee.setId(6);
        employee.setLastName("fuckyou");
        employee.setEmail("123@qq.com");
        employee.setGender(1);
        employee.setDepartment(department);
        Date date = new Date(2020-02-02);
        employee.setBirth(date);

        redisUtil.set("xiaohuang",employee);
        System.out.println(redisUtil.get("xiaohuang"));
    }
}
