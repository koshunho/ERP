package com.huang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

//员工表
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private Integer id;
    private String lastName;
    private String email;
    private Integer gender; //0代表女 1代表男
    private Department department;  //多对一。多个员工，对应一个部门

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birth;

//    public Employee(Integer id, String lastName, String email, Integer gender, Department department) {
//        this.id = id;
//        this.lastName = lastName;
//        this.email = email;
//        this.gender = gender;
//        this.department = department;
//        //默认的创建日期
//        this.birth = new Date();
//    }
}
