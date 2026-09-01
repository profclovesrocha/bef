package com.docmanager.version;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.docmanager.version", "com.docmanager.common"})
public class VersionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VersionServiceApplication.class, args);
    }
}
