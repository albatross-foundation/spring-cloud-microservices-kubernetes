package com.instagram.auth;

import com.instagram.auth.message.UserEventStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
@EnableEurekaClient
@EnableBinding(UserEventStream.class)
public class InstagramAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstagramAuthApplication.class, args);
    }

}
