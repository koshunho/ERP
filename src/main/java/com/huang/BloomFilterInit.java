package com.huang;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.huang.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BloomFilterInit implements ApplicationRunner {

    private BloomFilter bloomFilter;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Integer> list = employeeMapper.getAllEmployeesId();
        if(list.size()>0){
            bloomFilter=bloomFilter.create(Funnels.integerFunnel(),list.size(),0.01);
            for(int i = 0; i < list.size(); i++){
                bloomFilter.put(list.get(i));
            }
            System.out.println("预热employeesId到布隆过滤器成功！");
        }
    }

    public BloomFilter getIntegerBloomFilter(){
        return bloomFilter;
    }
}
