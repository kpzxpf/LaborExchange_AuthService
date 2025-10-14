package com.vlz.laborexchange_authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class LaborExchangeAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaborExchangeAuthServiceApplication.class, args);
    }

}
