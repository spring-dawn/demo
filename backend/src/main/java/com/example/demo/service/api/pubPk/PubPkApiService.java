package com.example.demo.service.api.pubPk;

import com.example.demo.domain.system.code.CodeRepository;
import com.example.demo.service.api.geoCoder.GeoCoderService;
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PubPkApiService {
    private final GeoCoderService geoCoderService;

    /* API 파일 저장소 */
    @Value("${spring.servlet.multipart2.json}")
    private String jsonDir;

    private final PubPkApiProperties properties;
    private final CodeRepository codeRepository;

    // 총 건수 가져오기
    public Integer requestTotalCount() throws UnsupportedEncodingException, JsonProcessingException {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        String BASE_URL = "http://api.data.go.kr/openapi/tn_pubr_prkplce_info_api";
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
                        .queryParam("type", "json")
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

    // 공영주차장 현황
    public String request() throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        String BASE_URL = "http://api.data.go.kr/openapi/tn_pubr_prkplce_info_api";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        int totalCount = requestTotalCount();
        int resultsPerPage = 1000;

        ObjectMapper objectMapper = new ObjectMapper();

        ArrayList<Map<String, String>> resultList = new ArrayList<>();

        for (int page = 1; page <= Math.ceil((double) totalCount / resultsPerPage); page++) {
            log.info("PubPk PAGE {}/{}",page * resultsPerPage, totalCount);
            try {
                int finalPage = page;

                String jsonResponse = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("serviceKey", properties.serviceKey)
                                .queryParam("pageNo", finalPage)
                                .queryParam("numOfRows", resultsPerPage)
                                .queryParam("type", "json")
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
                        resultList.add(itemMap);
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                page--;
            }

        }

        // 주소 => x,y
        for (Map<String, String> itemMap : resultList) {
            String longitude = itemMap.get("longitude");
            String latitude = itemMap.get("latitude");

            if (longitude.isEmpty() || latitude.isEmpty()) {
                String addr = itemMap.get("lnmadr");
                HashMap<String, String> coordMap = geoCoderService.request(addr, "PARCEL");
                itemMap.put("longitude", coordMap.get("lon"));
                itemMap.put("latitude", coordMap.get("lat"));
            }
        }

        String jsonString = objectMapper.writeValueAsString(resultList);

        return jsonString;
    }

    // 파일저장
    public boolean saveFile(String jsonData) throws UnsupportedEncodingException {
        if (jsonData != null) {
            // 파일 경로 생성 (파일 이름은 고유하게 설정)
            String fileName = "pubPkOpenJson.json";
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
            FileSystemResource resource = new FileSystemResource(jsonDir + "pubPkOpenJson.json");
            byte[] jsonData = Files.readAllBytes(Paths.get(resource.getFile().getAbsolutePath()));

            String jsonContent = new String(jsonData);

            ObjectMapper objectMapper = new ObjectMapper();

            ArrayList arrayList = objectMapper.readValue(jsonContent, ArrayList.class);

            HashMap<String, Object> objectObjectHashMap = new HashMap<String, Object>();
            objectObjectHashMap.put("data", arrayList);

            return objectObjectHashMap;

        } catch (IOException e) {
            log.error("파일이 없습니다 : pubPkOpenJson.json");

            return null;
        }
    }
}
