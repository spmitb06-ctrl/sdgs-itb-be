package com.sdgs.itb;

import com.sdgs.itb.infrastructure.config.JwtConfigProperties;
import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@Log
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication
@EnableConfigurationProperties(JwtConfigProperties.class)
@EnableCaching
@EnableScheduling
public class SDGSITBApplication {
	public static void main(String[] args) {
		SpringApplication.run(SDGSITBApplication.class, args);
	}
}