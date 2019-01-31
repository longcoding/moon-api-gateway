package com.longcoding.undefined;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

/**
 * Application settings for api-gateway.
 * The project added a classpath through the properties.
 * Because config yml for apis, apps can not be read as an existing classpath.
 *
 * EnableScheduling annotation exists for cluster mode.
 * The cluster mode synchronizes information through interval scheduling.
 *
 * For reference, the project is implemented based on interceptors.
 *
 * @author longcoding
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
