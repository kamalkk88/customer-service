package com.hotel.customerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {

		new SpringApplicationBuilder()
				.profiles("dev").sources(CustomerServiceApplication.class)
				.run(args);
	}

}
