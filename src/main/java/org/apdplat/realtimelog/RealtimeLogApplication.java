package org.apdplat.realtimelog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Created by ysc on 01/07/2018.
 */
@SpringBootApplication
public class RealtimeLogApplication extends SpringBootServletInitializer {
	public static final long START_TIME = System.currentTimeMillis();

	public static void main(String[] args) {
		SpringApplication.run(RealtimeLogApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RealtimeLogApplication.class);
	}
}
