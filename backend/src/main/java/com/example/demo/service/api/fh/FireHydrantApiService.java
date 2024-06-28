package com.example.demo.service.api.fh;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FireHydrantApiService {

    /* API 파일 저장소 */
    @Value("${spring.servlet.multipart2.json}")
    private String jsonDir;

    private final FireHydrantApiProperties properties;

    // 총 건수 가져오기
    public String requestTotalCount(String uuid) throws UnsupportedEncodingException, JsonProcessingException {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        String BASE_URL = "http://api.odcloud.kr/api" + uuid;
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        String jsonResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("serviceKey", properties.serviceKey)
                        .queryParam("page", 1)
                        .queryParam("perPage", 1)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        JsonNode pathsNode = jsonNode.path("totalCount");

        return String.valueOf(pathsNode.asInt());
    }

    // api uuid 가져오기
    public String requestUUID() throws UnsupportedEncodingException, JsonProcessingException {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        String BASE_URL = "http://infuser.odcloud.kr/oas/docs";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        String jsonResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("namespace", "15080958/v1")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ArrayList<String> pathList = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        // Get the path based on the JSON structure
        JsonNode pathsNode = jsonNode.path("paths");

        pathsNode.fields().forEachRemaining(entry -> {
            String path = entry.getKey();
            pathList.add(path);
        });

        return pathList.get(pathList.size() - 1);
    }

    // 보호구역
    public String request() throws UnsupportedEncodingException, JsonProcessingException {
        String requestUUID = requestUUID();

        String totalCount = requestTotalCount(requestUUID);

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        String BASE_URL = "http://api.odcloud.kr/api" + requestUUID;
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("serviceKey", properties.serviceKey)
                        .queryParam("page", 1)
                        .queryParam("perPage", totalCount)
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
            String fileName = "fireHydrantOpenJson.json";
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
            FileSystemResource resource = new FileSystemResource(jsonDir + "fireHydrantOpenJson.json");
            byte[] jsonData = Files.readAllBytes(Paths.get(resource.getFile().getAbsolutePath()));

            String jsonContent = new String(jsonData);

            ObjectMapper objectMapper = new ObjectMapper();

            HashMap<String, Object> jsonMap = objectMapper.readValue(jsonContent, HashMap.class);

            return jsonMap;

        } catch (IOException e) {
            log.error("파일이 없습니다 : fireHydrantOpenJson.json");

            return null;
        }
    }
}
