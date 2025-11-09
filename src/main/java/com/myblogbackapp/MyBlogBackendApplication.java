package com.myblogbackapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MyBlogBackendApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MyBlogBackendApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MyBlogBackendApplication.class);
    }
}
