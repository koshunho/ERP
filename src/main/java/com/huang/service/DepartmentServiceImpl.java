package com.huang.service;

import com.huang.mapper.DepartmentMapper;
import com.huang.pojo.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "departmentCache")
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
