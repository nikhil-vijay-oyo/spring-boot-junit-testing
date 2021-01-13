package com.oyo.testloglevel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.oyo.testloglevel", "com.oyo.component"})
public class TestLogLevelApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestLogLevelApplication.class, args);
    }

}
