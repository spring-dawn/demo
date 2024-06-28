package com.example.demo.atech;

import com.example.demo.dto.data.illegal.FineLedgerDto;
import com.example.demo.dto.data.illegal.ReceiveDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
public class IllegalDataApiConfig {
    /*
    차세대 세외수입행정 시스템에 연계(->불법주정차 단속 데이터)하는 WebClient 설정이 너무 복잡해져서 빈으로 등록해두고 사용합니다.
    builder 로 만들어진 WebClient 객체는 이후 변경불가. mutate() 를 통한 복사, 새 설정 추가만 가능합니다.
    @Bean 은 기본적으로 메서드명을 따라 생성되니 범용적으로 다른 팀원이 사용한 WebClient 인스턴스와 이름이 겹치지 않게 짓습니다.

    json 기준 단건 수신인 경우 mono, 다건 수신인 경우 flux.
    dto 필드명을 응답 내용과 일치시키면 자동 바인딩.
     */

    @Value("${illegal-data-api.url}")
    private String baseUrl;

    @Bean
    public WebClient template() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json;charset=UTF-8")
                .defaultHeader("Accept-Charset", "UTF-8")
                .build();
    }


    /**
     * 주정차위반 과태료 대장 목록 조회 요청
     *
     * @param header ifId 등 요청 헤더
     * @param body   각 연계 ID 에 맞는 업무 요청 파라미터
     * @return 다건 수신(Flux)
     */
    public Flux<FineLedgerDto> callApi2FineLedger(WebClient webClient, Map<String, String> header, Map<String, Object> body) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("header", header);
        request.put("body", body);

        return webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(FineLedgerDto.class)
//                .retry(3) // 요청이 실패하더라도 3번까지 재시도함
                .retryWhen(Retry.backoff(1, Duration.ofSeconds(3L)))    // 요청이 실패한 경우 3초 간격으로 최대 1번까지 재요청함.
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> log.error("Error! 주정차위반 과태료 대장 목록 조회 요청 중에 오류가 있습니다.", error));
    }

    /**
     * 수납상세정보 조회 요청
     *
     * @param header ifId 등 요청 헤더
     * @param body   각 연계 ID 에 맞는 업무 요청 파라미터
     * @return 다건 수신(Flux)
     */
    public Flux<ReceiveDetailDto> callApi2ReceiveDetail(WebClient webClient, Map<String, String> header, Map<String, Object> body) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("header", header);
        request.put("body", body);

        return webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(ReceiveDetailDto.class)
                .retryWhen(Retry.backoff(1, Duration.ofSeconds(3L)))
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> log.error("Error! 수납상세정보 조회 요청 중에 오류가 있습니다.", error));
    }


}
