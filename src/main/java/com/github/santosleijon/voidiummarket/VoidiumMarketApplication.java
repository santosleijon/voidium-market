package com.github.santosleijon.voidiummarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan("com.github.santosleijon.voidiummarket")
public class VoidiumMarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoidiumMarketApplication.class, args);
	}

}
