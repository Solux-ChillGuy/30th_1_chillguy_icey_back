package com.project.icey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IceyApplication {

	public static void main(String[] args) {
		SpringApplication.run(IceyApplication.class, args);
	}

}
