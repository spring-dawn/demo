package com.example.demo.service.api.kosis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KosisService {

    // 구군별 총 인구수
    public List<Map<String, String>> requestPop(String startPrdDe, String endPrdDe) throws UnsupportedEncodingException, JsonProcessingException {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        String BASE_URL = "https://kosis.kr/openapi/Param/statisticsParameterData.do?method=getList&apiKey=M2JjNDMxZDg0NzJkYTUyYTlkNjE5YzM1Yzc0NmEyODI=&itmId=001+003+&objL1=001+002+003+004+005+006+&objL2=001+&objL3=&objL4=&objL5=&objL6=&objL7=&objL8=&format=json&jsonVD=Y&prdSe=M&outputFields=ORG_ID+TBL_ID+TBL_NM+OBJ_ID+OBJ_NM+OBJ_NM_ENG+NM+NM_ENG+ITM_ID+ITM_NM+ITM_NM_ENG+UNIT_NM+UNIT_NM_ENG+PRD_SE+PRD_DE+&orgId=207&tblId=DT_20703_001";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        String jsonResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("startPrdDe", startPrdDe)
                        .queryParam("endPrdDe", endPrdDe)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        // Create a list to hold the maps
        List<Map<String, String>> dataList = new ArrayList<>();

        // Iterate over each object in the JSON array
        for (JsonNode node : jsonNode) {
            // Extract the required fields
            String sgg = node.path("C1_NM").asText();
            String dt = node.path("DT").asText();
            String type = node.path("ITM_NM").asText();
            String date = node.path("PRD_DE").asText();

            // Create a map to store the extracted values
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("type", type);
            dataMap.put("sgg", sgg);
            dataMap.put("cnt", dt);
            dataMap.put("date", date);

            // Add the map to the list
            dataList.add(dataMap);
        }

        // Print or process the list of maps as needed
        for (Map<String, String> data : dataList) {
            log.info("Data={}", data);
//            System.out.println("Data: " + data);
        }

        return dataList;
    }

    // 구군별 차량등록대수 (가장최신)
    public List<Map<String, String>> requestCar(String startPrdDe, String endPrdDe) throws UnsupportedEncodingException, JsonProcessingException {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        String BASE_URL = "https://kosis.kr/openapi/Param/statisticsParameterData.do?method=getList&apiKey=M2JjNDMxZDg0NzJkYTUyYTlkNjE5YzM1Yzc0NmEyODI=&itmId=13103873443T4+&objL1=13102873443A.0007+&objL2=13102873443B.0002+13102873443B.0025+13102873443B.0029+13102873443B.0030+13102873443B.0033+13102873443B.0055+&objL3=13102873443C.0005+&objL4=&objL5=&objL6=&objL7=&objL8=&format=json&jsonVD=Y&prdSe=M&outputFields=ORG_ID+TBL_ID+TBL_NM+OBJ_ID+OBJ_NM+OBJ_NM_ENG+NM+NM_ENG+ITM_ID+ITM_NM+ITM_NM_ENG+UNIT_NM+UNIT_NM_ENG+PRD_SE+PRD_DE+&orgId=116&tblId=DT_MLTM_5498";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        String jsonResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("startPrdDe", startPrdDe)
                        .queryParam("endPrdDe", endPrdDe)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        // Create a list to hold the maps
        List<Map<String, String>> dataList = new ArrayList<>();

        // Iterate over each object in the JSON array
        for (JsonNode node : jsonNode) {
            // Extract the required fields
            String sgg = node.path("C2_NM").asText();
            String dt = node.path("DT").asText();
            String date = node.path("PRD_DE").asText();

            // Create a map to store the extracted values
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("sgg", sgg);
            dataMap.put("cnt", dt);
            dataMap.put("date", date);

            // Add the map to the list
            dataList.add(dataMap);
        }

        return dataList;
    }

    // 구군별 차량등록대수 (최신일 기준 12개월)
    public List<Map<String, String>> requestMonthCar() throws UnsupportedEncodingException, JsonProcessingException {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        String BASE_URL = "https://kosis.kr/openapi/Param/statisticsParameterData.do?method=getList&apiKey=M2JjNDMxZDg0NzJkYTUyYTlkNjE5YzM1Yzc0NmEyODI=&itmId=13103873443T4+&objL1=13102873443A.0007+&objL2=13102873443B.0025+13102873443B.0029+13102873443B.0030+13102873443B.0033+13102873443B.0055+&objL3=13102873443C.0005+&objL4=&objL5=&objL6=&objL7=&objL8=&format=json&jsonVD=Y&prdSe=M&newEstPrdCnt=12&prdInterval=1&outputFields=ORG_ID+TBL_ID+TBL_NM+OBJ_ID+OBJ_NM+OBJ_NM_ENG+NM+NM_ENG+ITM_ID+ITM_NM+ITM_NM_ENG+UNIT_NM+UNIT_NM_ENG+PRD_SE+PRD_DE+&orgId=116&tblId=DT_MLTM_5498";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        String jsonResponse = webClient.get()
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        // Create a list to hold the maps
        List<Map<String, String>> dataList = new ArrayList<>();

        // Iterate over each object in the JSON array
        for (JsonNode node : jsonNode) {
            // Extract the required fields
            String sgg = node.path("C2_NM").asText();
            String date = node.path("PRD_DE").asText();
            String dt = node.path("DT").asText();

            // Create a map to store the extracted values
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("sgg", sgg);
            dataMap.put("cnt", dt);
            dataMap.put("date", date);

            // Add the map to the list
            dataList.add(dataMap);
        }

        // Print or process the list of maps as needed
        for (Map<String, String> data : dataList) {
            log.info("Data={}", data);
//            System.out.println("Data: " + data);
        }

        return dataList;
    }
}
