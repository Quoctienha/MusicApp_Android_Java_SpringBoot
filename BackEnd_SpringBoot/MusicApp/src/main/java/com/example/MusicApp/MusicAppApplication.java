package com.example.MusicApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication //(scanBasePackages = "com.example.MusicApp")
@ComponentScan(basePackages = "com.example.MusicApp") // bảo đảm nó quét cả package con
@EnableAutoConfiguration
public class MusicAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicAppApplication.class, args);
		
	}

}
