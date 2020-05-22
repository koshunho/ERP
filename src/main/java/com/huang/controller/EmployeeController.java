package com.huang.controller;

import com.huang.dao.DepartmentDao;
import com.huang.dao.EmployeeDao;
import com.huang.pojo.Department;
import com.huang.pojo.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@Controller
public class EmployeeController {
    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    private DepartmentDao departmentDao;

    @RequestMapping("/emps")
    public String list(Model model){
        Collection<Employee> employees = employeeDao.getAll();
        model.addAttribute("emps",employees);
        return "emp/list";
    }

    @GetMapping("/emp")
    public String toAdd(Model model){
        //查出部门的信息
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("departments",departments);
        return "emp/add";
    }

    @PostMapping("/emp")
    public String addEmp(Employee employee){
        System.out.println("addEmp:"+ employee);
        employeeDao.save(employee);
        return "redirect:/emps";
    }

    //去到员工的修改页面
    @GetMapping("/emp/{id}")
    public String toUpdateEmp(@PathVariable("id")Integer id,Model model){
        Employee employee = employeeDao.getEmployeeById(id);
        model.addAttribute("emp",employee);
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("departments",departments);
        return "emp/update";
    }

    @PostMapping("/updateEmp")
    public String updateEmp(Employee employee){
        System.out.println("updateEmp:" + employee);
        employeeDao.save(employee);
        return "redirect:/emps";
    }

    @GetMapping("/delete/{id}")
    public String toDeteleEmp(@PathVariable("id")Integer id){
        employeeDao.delete(id);
        return "redirect:/emps";
    }
}
