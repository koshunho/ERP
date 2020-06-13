package com.huang.service;

import com.huang.mapper.EmployeeMapper;
import com.huang.pojo.Employee;
import com.huang.BloomFilterInit;
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

    @Autowired
    private BloomFilterInit bloomFilterInit;

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


    /**
     *
     * @param id
     * @return com.huang.pojo.Employee
     * @author Koshunho
     * @date 2020/6/13 18:22
     */

    @Override
    public Employee selectEmployeeById(int id) {
        String key = "Employee" + id;

        //boolean hasKey = redisUtil.hasKey(key);

/*        if(hasKey){
            Employee employee = (Employee) redisUtil.get(key);
            System.out.println("从redis中获取了Employee" + id);
            return employee;
        }
        Employee employee = employeeMapper.selectEmployeeById(id);
        redisUtil.set(key, employee, 500);
        System.out.println("从db中获取Employee"+id);
        return employee;*/

/*        if(hasKey == false){
            Employee employee = employeeMapper.selectEmployeeById(id);
            redisUtil.set(key, employee);
            if(employee==null){
                redisUtil.expire(key, 300);
            }
            return employee;
        }
        return (Employee)redisUtil.get(key);*/

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

        bloomFilterInit.getIntegerBloomFilter().put(employee.getId());
        return ret;
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

        redisUtil.del(key,"allEmployee");

        System.out.println("删除:从redis中删除了全部的Employee");

        return ret;
    }

    @Override
    public List<Integer> getAllEmployeesId() {
        return employeeMapper.getAllEmployeesId();
    }
}
