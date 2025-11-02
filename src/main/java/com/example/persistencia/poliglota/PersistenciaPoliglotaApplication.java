package com.example.persistencia.poliglota;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages = "com.example.persistencia.poliglota")
@EnableScheduling
public class PersistenciaPoliglotaApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersistenciaPoliglotaApplication.class, args);
    }
}
