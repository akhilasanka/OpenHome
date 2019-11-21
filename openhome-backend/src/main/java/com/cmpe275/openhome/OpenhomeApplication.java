package com.cmpe275.openhome;

import com.cmpe275.openhome.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class OpenhomeApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenhomeApplication.class, args);
		
		System.out.println("Server has started running on port 8080");
	}

}
