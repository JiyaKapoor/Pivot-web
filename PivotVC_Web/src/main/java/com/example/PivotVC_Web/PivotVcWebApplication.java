package com.example.PivotVC_Web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PivotVcWebApplication {
	public static void main(String[] args) {
		SpringApplication.run(PivotVcWebApplication.class, args);
	}
}
