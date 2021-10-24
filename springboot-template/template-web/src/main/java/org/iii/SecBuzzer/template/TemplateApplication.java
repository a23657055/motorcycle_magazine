package org.iii.SecBuzzer.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
@ServletComponentScan
public class TemplateApplication {	
	public static void main(String[] args) {
		SpringApplication.run(TemplateApplication.class, args);
	}
}
