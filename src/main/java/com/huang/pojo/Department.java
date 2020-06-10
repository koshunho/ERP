package com.huang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//部门表
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department implements Serializable {
    private Integer id;
    private String DepartmentName;
}
