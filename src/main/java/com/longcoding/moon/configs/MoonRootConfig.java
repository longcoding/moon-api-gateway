package com.longcoding.moon.configs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Contains settings for the root application context.
 * The project defined a package for component scan.
 *
 * @author longcoding
 */

@Configuration
@ComponentScan({ "com.longcoding.moon" })
public class MoonRootConfig { }
