package ru.pulsecore.app;


import org.springframework.cache.annotation.EnableCaching;
import ru.pulsecore.app.modules.shared.propirties.AdminProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;


@SpringBootApplication
@EnableConfigurationProperties(AdminProperties.class)
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