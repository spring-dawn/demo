package com.example.demo.service.api.pa;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProtectedAreaApiService {

    /* API 파일 저장소 */
    @Value("${spring.servlet.multipart2.json}")
    private String jsonDir;

    private final ProtectedAreaApiProperties properties;

    // 보호구역
    public String request(String sigunguCd) throws UnsupportedEncodingException {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        String BASE_URL = "http://www.utic.go.kr/guide/getSafeOpenJson.do";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", properties.serviceKey)
                        .queryParam("sidoCd", sigunguCd)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response;
    }

    // 파일저장
    public boolean saveFile(String jsonData) throws UnsupportedEncodingException {
        if (jsonData != null) {
            // 파일 경로 생성 (파일 이름은 고유하게 설정)
            String fileName = "safeOpenJson.json";
            String filePath = jsonDir + fileName;

            // JSON 데이터를 파일로 저장
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                writer.write(jsonData);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    public HashMap<String, Object> getData() {
        try {
            FileSystemResource resource = new FileSystemResource(jsonDir + "safeOpenJson.json");
            byte[] jsonData = Files.readAllBytes(Paths.get(resource.getFile().getAbsolutePath()));

            String jsonContent = new String(jsonData);

            ObjectMapper objectMapper = new ObjectMapper();

            HashMap<String, Object> jsonMap = objectMapper.readValue(jsonContent, HashMap.class);

            return jsonMap;

        } catch (IOException e) {
            log.error("파일이 없습니다 : safeOpenJson.json");

            return null;
        }
    }
}
