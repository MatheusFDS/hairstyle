package com.fiap.hairstyle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.fiap.hairstyle")  // Garante o escaneamento de todos os pacotes
@EntityScan(basePackages = "com.fiap.hairstyle.dominio.entidades")
@EnableScheduling
public class HairstylefiapApplication {
	public static void main(String[] args) {
		SpringApplication.run(HairstylefiapApplication.class, args);
	}
}
