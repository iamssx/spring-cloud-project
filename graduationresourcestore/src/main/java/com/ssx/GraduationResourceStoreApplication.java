package com.ssx;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class GraduationResourceStoreApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(GraduationResourceStoreApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
	}
}
