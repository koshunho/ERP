package com.huang.service;

import com.huang.pojo.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> selectAllDepartment();

    Department selectDepartmentById(int id);
}
