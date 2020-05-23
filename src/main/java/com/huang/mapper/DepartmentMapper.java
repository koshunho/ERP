package com.huang.mapper;

import com.huang.pojo.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DepartmentMapper {
    List<Department> selectAllDepartment();

    Department selectDepartmentById(@Param("id") int id);
}
