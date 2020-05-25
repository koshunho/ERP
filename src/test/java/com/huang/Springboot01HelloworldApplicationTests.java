package com.huang;

import com.huang.pojo.Department;
import com.huang.pojo.Employee;
import com.huang.pojo.User;
import com.huang.service.DepartmentService;
import com.huang.service.EmployeeService;
import com.huang.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@SpringBootTest
class Springboot01HelloworldApplicationTests {

    @Autowired
    DataSource dataSource;

    @Test
    void contextLoads() throws SQLException {
        System.out.println("数据源>>>>>>" + dataSource.getClass());
        Connection connection = dataSource.getConnection();
        System.out.println("连接>>>>>>>>>" + connection);
        System.out.println("连接地址>>>>>" + connection.getMetaData().getURL());
        connection.close();
    }

    @Autowired
    EmployeeService employeeService;
    @Autowired
    DepartmentService departmentService;

    @Test
    public void testEmployeeService(){
        List<Employee> list = employeeService.selectAllEmployee();
        for(Employee employee:list){
            System.out.println(employee);
        }
    }

    @Test
    public void test1(){
        Employee employee = employeeService.selectEmployeeById(1);
        System.out.println(employee);
    }

    @Test
    public void test2(){
        Department department = departmentService.selectDepartmentById(1);
        System.out.println(department);
    }

    @Test
    public void test3(){
        Department department = departmentService.selectDepartmentById(1);
        Employee employee = new Employee();
        //employee.setId(6);
        employee.setLastName("fuckyou");
        employee.setEmail("123@qq.com");
        employee.setGender(1);
        employee.setDepartment(department);
        Date date = new Date(2020-02-02);
        employee.setBirth(date);
        employeeService.addEmployee(employee);
    }

    @Test
    public void test4(){
        employeeService.deleteEmployee(21);
    }

    @Autowired
    private UserService userService;
    @Test
    public void test5(){
        User admin = userService.selectUserByName("admin","123456");
        System.out.println(admin);
        //User(id=1, name=admin, password=123456)
    }


    @Test
    public void test6() {
        List<Department> departments = departmentService.selectAllDepartment();
        for (Department department : departments) {
            System.out.println(department);
        }
    }

    @Test
    public void test7(){
        System.out.println(userService.selectUserByShiro("admin"));
    }
}
