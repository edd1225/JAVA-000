package com.qj.week7;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName App
 * @Author edd1225
 * @Version V1.0
 **/
@SpringBootApplication
@MapperScan("com.qj.week7.mapper")
public class AppUser {
    public static void main(String[] args) {
        SpringApplication.run(AppUser.class);
    }
}
