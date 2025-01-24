package org.example.doctorai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DoctorAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoctorAiApplication.class, args);
    }

}
