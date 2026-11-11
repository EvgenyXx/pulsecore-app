package ru.pulsecore.app;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.TimeZone;


@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@EnableAsync
@EnableCaching
public class PulsecoreApplication {

	public static void main(String[] args)  {


		SpringApplication.run(PulsecoreApplication.class, args);

	}
	@PostConstruct
	void setTimezone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
	}
}