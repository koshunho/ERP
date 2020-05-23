package com.huang.service;

import com.huang.mapper.DepartmentMapper;
import com.huang.pojo.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService{

    @Autowired
    private DepartmentMapper departmentMapper;
    @Override
    public List<Department> selectAllDepartment() {
        return departmentMapper.selectAllDepartment();
    }

    @Override
    public Department selectDepartmentById(int id) {
        return departmentMapper.selectDepartmentById(id);
    }
}
