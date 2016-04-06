package com.longcoding.undefined.configs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by longcoding on 16. 4. 5..
 */

@EnableWebMvc
@Configuration
@ComponentScan({ "com.longcoding.undefined" })
public class UndefinedRootConfig { }
