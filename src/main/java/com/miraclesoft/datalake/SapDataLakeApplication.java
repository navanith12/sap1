package com.miraclesoft.datalake;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableAsync
@EnableScheduling
@EnableAutoConfiguration
public class SapDataLakeApplication{

	 private static ConfigurableApplicationContext context;

	    public static void main(String[] args) {
	        context = SpringApplication.run(SapDataLakeApplication.class, args);
	    }

	    public static void restart() {
	        ApplicationArguments args = context.getBean(ApplicationArguments.class);

	        Thread thread = new Thread(() -> {
	            context.close();
	            context = SpringApplication.run(SapDataLakeApplication.class, args.getSourceArgs());
	        });

	        thread.setDaemon(false);
	        thread.start();
	    }

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.miraclesoft.datalake")).build();
	}
}