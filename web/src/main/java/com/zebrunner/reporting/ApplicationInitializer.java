package com.zebrunner.reporting;

import org.apache.commons.io.output.NullOutputStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.PrintStream;

@SpringBootApplication
public class ApplicationInitializer {

    public static void main(String[] args) {
        System.setOut(new PrintStream(new NullOutputStream()));
        SpringApplication.run(ApplicationInitializer.class, args);
    }

}
