package com.huang.service;

import com.huang.pojo.User;

public interface UserService {
    User selectUserByName(String username, String password);

    User selectUserByShiro(String username);
}
