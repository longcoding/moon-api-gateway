package com.longcoding.undefined;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
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
        //SpringApplication.run(UndefinedApplication.class, args);
        new SpringApplicationBuilder(UndefinedApplication.class)
                .properties("spring.config.location:classpath:/apps/, classpath:/apis/, classpath:/")
                .build().run(args);



    }

}
