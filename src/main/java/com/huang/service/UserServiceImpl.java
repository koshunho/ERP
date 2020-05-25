package com.huang.service;

import com.huang.mapper.UserMapper;
import com.huang.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectUserByName(String username, String password) {
        return userMapper.selectUserByName(username,password);
    }

    @Override
    public User selectUserByShiro(String username) {
        return userMapper.selectUserByShiro(username);
    }
}
