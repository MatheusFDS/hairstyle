package com.fiap.hairstyle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principal da aplicação Hairstylefiap.
 *
 * Inicializa o contexto do Spring Boot e configura a aplicação, incluindo o escaneamento de componentes
 * e entidades, além da habilitação do agendamento de tarefas.
 *
 * <p>Principais Anotações:</p>
 * <ul>
 *   <li>@SpringBootApplication: Marca a aplicação como uma aplicação Spring Boot.</li>
 *   <li>@ComponentScan: Garante que todos os pacotes dentro de 'com.fiap.hairstyle' sejam escaneados para componentes.</li>
 *   <li>@EntityScan: Garante que todas as entidades dentro de 'com.fiap.hairstyle.dominio.entidades' sejam detectadas.</li>
 *   <li>@EnableScheduling: Habilita a funcionalidade de agendamento para tarefas no Spring.</li>
 * </ul>
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.fiap.hairstyle") // Garante o escaneamento de todos os pacotes
@EntityScan(basePackages = "com.fiap.hairstyle.dominio.entidades")
@EnableScheduling
public class HairstylefiapApplication {

	/**
	 * Método principal que inicializa a aplicação.
	 *
	 * @param args argumentos de linha de comando (não utilizados atualmente)
	 */
	public static void main(String[] args) {
		SpringApplication.run(HairstylefiapApplication.class, args);
	}
}
