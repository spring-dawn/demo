//package com.example.demo.atech;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.web.reactive.function.client.WebClient;
//
//public class IllegalWebClientConfig {
//    /*
//    차세대 세외수입행정 특화시스템 연계 == 불법주정차 단속 데이터
//    를 요청할 WebClient 를 미리 빈으로 등록하여 사용. build 로 세팅하면 이후 설정 변경이 불가능합니다.
//    인스턴스.mutate() 로 필요한 설정을 추가할 수는 있습니다.
//
//    요청은 post.
//    고정 디폴트 헤더는 application.yml 환경설정 파일 사용, 그 외 날짜 등 동적인 파라미터는 로직상 지정.
//
//    응답 json 은 flux(다중 객체)로 예상, DB 물리명과 별도로 dto 가 바로 바인딩 될 수 있게
//    json - dto 명을 일치시킵니다.
//
//     */
//
//    @Value("${spring.illegal-data-link.host.prod}")
//    private String prod;
//
//    @Value("${spring.illegal-data-link.host.validation}")
//    private String validation;
//
//    @Value("${spring.illegal-data-link.port}")
//    private String port;
//
//    private final String BASE_URL_PROD = "https://" + prod + ":" + port + "/mediate/ltis";
//    private final String BASE_URL_VALIDATION = "https://" + validation + ":" + port + "/mediate/ltis";
//
//    HttpHeaders headers = new HttpHeaders();
//
//    @Bean
//    public WebClient prodReq() {
//        return WebClient.builder()
//                .baseUrl(BASE_URL_PROD)
//                .defaultHeaders(httpHeaders -> httpHeaders.addAll(headers))
//                .build();
//    }
//
//    @Bean
//    public WebClient validationReq() {
//        return WebClient.builder()
//                .baseUrl(BASE_URL_VALIDATION)
//                .defaultHeaders(httpHeaders -> httpHeaders.addAll(headers))
//                .build();
//    }
//
//
//}
