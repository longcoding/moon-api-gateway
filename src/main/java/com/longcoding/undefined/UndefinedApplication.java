package com.longcoding.undefined;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

/**
 * Created by longcoding on 18. 12. 26..
 */

@EnableScheduling
@SpringBootApplication
public class UndefinedApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(UndefinedApplication.class, args);
    }

}
