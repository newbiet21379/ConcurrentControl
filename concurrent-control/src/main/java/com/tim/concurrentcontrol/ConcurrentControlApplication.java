package com.tim.concurrentcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
@SpringBootApplication
@EnableRetry
public class ConcurrentControlApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConcurrentControlApplication.class, args);
	}

}
