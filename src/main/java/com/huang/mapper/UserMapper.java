package com.huang.mapper;

import com.huang.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    User selectUserByName(@Param("username")String username, @Param("password")String password);

    User selectUserByShiro(@Param("username")String username);
}
