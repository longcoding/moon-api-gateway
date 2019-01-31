package com.longcoding.undefined.configs;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * It is a swagger configuration for internal api.
 * With the internal API, you can register, change, or delete a new API.
 * app registration, change deletion.
 * service registration, change deletion can be done.
 *
 * @author longcoding
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.longcoding.undefined.controllers"))
                .paths(paths())
                .build();
    }

    private Predicate<String> paths() {
        return Predicates.not(PathSelectors.regex("/basic-error-controller.*"));
    }
}
