package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
//开启服务端
@EnableEurekaServer
public class LyRegistry {
    public static void main(String[] args) {

        SpringApplication.run(LyRegistry.class,args);
    }
}
