package com.vishwesh.jwt_authentication_series_01;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JwtAuthenticationSeries01Application {

	public static void main(String[] args) {
		SpringApplication.run(JwtAuthenticationSeries01Application.class, args);
	}

	@Bean
	CommandLineRunner runner(
			@Value("${spring.security.user.name}") String username,
			@Value("${spring.security.user.password}") String password) {

		return args -> {
			System.out.println("Username = " + username);
			System.out.println("Password = " + password);
		};
	}
}
