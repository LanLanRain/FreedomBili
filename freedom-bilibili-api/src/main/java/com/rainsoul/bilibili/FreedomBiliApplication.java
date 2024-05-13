package com.rainsoul.bilibili;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class FreedomBiliApplication {
    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(FreedomBiliApplication.class, args);
    }
}
