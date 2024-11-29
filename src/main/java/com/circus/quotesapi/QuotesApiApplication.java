package com.circus.quotesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
public class QuotesApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(QuotesApiApplication.class, args);
	}
}
