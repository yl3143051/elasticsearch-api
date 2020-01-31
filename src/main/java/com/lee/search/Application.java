package com.lee.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@EnableAutoConfiguration
@ServletComponentScan
//@ComponentScan(basePackages = {"com.lee.search"})
public class Application {
    public static void main(String[] args) {
    	SpringApplication.run(Application.class, args);
    }
}