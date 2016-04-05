package com.longcoding.undefined.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.Filter;

/**
 * Created by longcoding on 16. 4. 5..
 */

@EnableWebMvc
@Configuration
@ComponentScan
public class UndefinedRootConfig { }
