package com.example.persistencia.poliglota;



import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@SpringBootApplication
public class PersistenciaPoliglotaApplication {

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(PersistenciaPoliglotaApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printEndpoints() {
        RequestMappingHandlerMapping mapping = context.getBean(RequestMappingHandlerMapping.class);
        mapping.getHandlerMethods().forEach((key, value) -> System.out.println(key));
    }
}
