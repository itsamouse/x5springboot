package net.jingbo.x5springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class X5springbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(X5springbootApplication.class, args);
	}
}
