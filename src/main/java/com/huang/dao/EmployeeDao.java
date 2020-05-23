//package com.huang.dao;
//
//import com.huang.pojo.Department;
//import com.huang.pojo.Employee;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
//@Repository
//public class EmployeeDao {
//    //模拟数据库中的数据
//    private static Map<Integer, Employee> employees = null;
//    @Autowired
//    private DepartmentDao departmentDao;
//    static{
//        employees = new HashMap<>();
//
////        employees.put(1001, new Employee(1001,"习近平","A12345@qq.com",0, new Department(106,"财务部")));
////        employees.put(1002, new Employee(1002,"李克强","B12345@qq.com",1, new Department(107,"军事部")));
////        employees.put(1003, new Employee(1003,"江泽民","C12345@qq.com",0, new Department(108,"中南海")));
////        employees.put(1004, new Employee(1004,"胡锦涛","D12345@qq.com",1, new Department(109,"农业部")));
////        employees.put(1005, new Employee(1005,"毛泽东","E12345@qq.com",0, new Department(110,"教育部")));
//    }
//
//    private static Integer initId = 1006;
//    //增加一个员工
//    public void save(Employee employee){
//        if(employee.getId() == null){
//            employee.setId(initId++);
//        }
//        employee.setDepartment(departmentDao.getDepartmentById(employee.getDepartment().getId()));
//        employees.put(employee.getId(),employee);
//    }
//
//    //查询全部员工
//    public Collection<Employee> getAll(){
//        return employees.values();
//    }
//
//    //通过ID查询员工
//    public Employee getEmployeeById(Integer id){
//        return employees.get(id);
//    }
//
//    //删除员工
//    public void delete(Integer id){
//        employees.remove(id);
//    }
//}
