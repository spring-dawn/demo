package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableCaching      // 캐싱 적용
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class DemoApplication {
    /*
    * 백엔드와 프론트엔드를 분리하였으나 일부는 restful과는 거리가 있습니다.
    * - restful하기 위해서는 클라이언트의 정보가 저장되지 않아야하나 현재 spring security 로 세션 정보를 저장 및 사용중입니다.
    * - 각각의 request 는 독립적이어야하나 마찬가지로 세션을 사용중이기 때문에 완전독립적이지 못합니다.
    * */

    @PostConstruct
    void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
