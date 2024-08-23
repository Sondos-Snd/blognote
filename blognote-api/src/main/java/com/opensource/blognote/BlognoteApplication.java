package com.opensource.blognote;

import com.opensource.blognote.role.Role;
import com.opensource.blognote.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
public class BlognoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlognoteApplication.class, args);
	}


	@Bean
	public CommandLineRunner runner (RoleRepository roleRrpository){
		return args -> {
			if (roleRrpository.findByName("USER").isEmpty()) {
				roleRrpository.save(
						Role.builder().name("USER").build()
				);
			}
		};
	}

}
