package com.lee.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan({"com.lee.controller", "com.lee.config"})
@EnableWebMvc
public class SpringMvcConfig {
}
