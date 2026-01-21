package org.roni.ronitrouble;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan("org.roni.ronitrouble.mapper")
public class RoniTroubleApplication {

    static void main(String[] args) {
        SpringApplication.run(RoniTroubleApplication.class, args);
    }

}
