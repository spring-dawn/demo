package com.example.demo.service.api.residnt;

import com.example.demo.domain.system.code.CodeRepository;
import com.example.demo.service.api.geoCoder.GeoCoderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResidntApiService {
    private final GeoCoderService geoCoderService;

    /* API 파일 저장소 */
    @Value("${spring.servlet.multipart2.json}")
    private String jsonDir;

    private final ResidntApiProperties properties;
    private final CodeRepository codeRepository;

    // 총 건수 가져오기
    public Integer requestTotalCount() throws UnsupportedEncodingException, JsonProcessingException {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        String BASE_URL = "http://api.data.go.kr/openapi/tn_pubr_public_residnt_prior_parkng_api";
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
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", 1)
                        .queryParam("type", "JSON")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        JsonNode responseNode = jsonNode.path("response");
        JsonNode bodyNode = responseNode.path("body");
        String total = bodyNode.path("totalCount").textValue();

        return Integer.parseInt(total);
    }

    // 거주자 우선 주차장 현황
    public String request() throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        String BASE_URL = "http://api.data.go.kr/openapi/tn_pubr_public_residnt_prior_parkng_api";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        int totalCount = requestTotalCount();
        int resultsPerPage = 100;

        ObjectMapper objectMapper = new ObjectMapper();

        ArrayList<Map<String, String>> resultList = new ArrayList<>();

        for (int page = 1; page <= Math.ceil((double) totalCount / resultsPerPage); page++) {
            log.info("Residnt PAGE {}/{}",page * resultsPerPage, totalCount);

            try {
                int finalPage = page;

                String jsonResponse = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("serviceKey", properties.serviceKey)
                                .queryParam("pageNo", finalPage)
                                .queryParam("numOfRows", resultsPerPage)
                                .queryParam("type", "JSON")
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                JsonNode jsonNode = objectMapper.readTree(jsonResponse);
                JsonNode responseNode = jsonNode.path("response");
                JsonNode bodyNode = responseNode.path("body");
                JsonNode itemsNode = bodyNode.path("items");

                Iterator<JsonNode> itemsIterator = itemsNode.elements();
                while (itemsIterator.hasNext()) {
                    JsonNode itemNode = itemsIterator.next();

                    // Convert itemNode to a HashMap
                    Map<String, String> itemMap = new LinkedHashMap<>();
                    Iterator<Map.Entry<String, JsonNode>> fieldsIterator = itemNode.fields();
                    while (fieldsIterator.hasNext()) {
                        Map.Entry<String, JsonNode> fieldEntry = fieldsIterator.next();
                        itemMap.put(fieldEntry.getKey(), fieldEntry.getValue().asText());
                    }

                    String addr = itemMap.get("lnmadr");
                    String[] split = addr.split(" ");

                    if (split[0].equals("울산광역시")) {
                        log.info("데이터: {}",itemMap.toString());
                        resultList.add(itemMap);
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                page--;
            }

        }

        String jsonString = objectMapper.writeValueAsString(resultList);

        return jsonString;
    }

    // 파일저장
    public boolean requestAndSaveJson(String jsonData) throws UnsupportedEncodingException {
        if (jsonData != null) {
            // 파일 경로 생성 (파일 이름은 고유하게 설정)
            String fileName = "residntOpenJson.json";
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
}
