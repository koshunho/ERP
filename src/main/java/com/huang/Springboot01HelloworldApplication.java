package com.huang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class Springboot01HelloworldApplication {

    public static void main(String[] args) {

        SpringApplication.run(Springboot01HelloworldApplication.class, args);
    }

}
