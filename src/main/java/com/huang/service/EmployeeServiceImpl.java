package com.huang.service;

import com.huang.mapper.EmployeeMapper;
import com.huang.pojo.Employee;
import com.huang.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


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

    @Override
    public Employee selectEmployeeById(int id) {
        String key = "Employee" + id;

        boolean hasKey = redisUtil.hasKey(key);

        if(hasKey){
            Employee employee = (Employee) redisUtil.get(key);
            System.out.println("从redis中获取了Employee" + id);
            return employee;
        }
        Employee employee = employeeMapper.selectEmployeeById(id);
        redisUtil.set(key, employee, 500);
        System.out.println("从db中获取Employee"+id);
        return employee;
    }

    @Override
    public int addEmployee(Employee employee) {
        return employeeMapper.addEmployee(employee);
    }

    @Override
    public int updateEmployee(Employee employee) {
        int ret = employeeMapper.updateEmployee(employee);

        String key = "Employee" + employee.getId();

        boolean hasKey = redisUtil.hasKey(key);

        if(hasKey){
            redisUtil.del(key,"allEmployee");
            System.out.println("更新:从redis中删除了全部的Employee");
        }
        return ret;
    }

    @Override
    public int deleteEmployee(int id) {
        int ret = employeeMapper.deleteEmployee(id);

        String key = "Employee" + id;

        boolean hasKey = redisUtil.hasKey(key);

        if(hasKey){
            redisUtil.del(key,"allEmployee");
            System.out.println("删除:从redis中删除了全部的Employee");
        }
        return ret;
    }
}
