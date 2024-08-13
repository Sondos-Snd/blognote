package com.opensource.blognote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BlognoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlognoteApplication.class, args);
	}

}
