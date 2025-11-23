package com.backend.lnuais_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LnuaisBackendApplication {

	public static void main(String[] args) {
		System.out.println("Backend Server Running...");
		SpringApplication.run(LnuaisBackendApplication.class, args);
	}

}
