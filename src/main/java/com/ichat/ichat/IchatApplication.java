package com.ichat.ichat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IchatApplication {

	public static void main(String[] args) {
		SpringApplication.run(IchatApplication.class, args);
	}

}
