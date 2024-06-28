package com.example.demo.service.api.geoCoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class GeoCoderService {

    public HashMap<String,String> request(String address, String type) throws UnsupportedEncodingException, JsonProcessingException {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        String BASE_URL = "http://api.vworld.kr/req/address";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        String jsonResponse = webClient.get()
                .uri(uriBuilder -> {
                    try {
                        return uriBuilder
                                .queryParam("key", "BDF41B1E-F857-3FC6-A201-FDF4D19B1F16")
//                                .queryParam("key", "6DF1C77E-47E8-3E51-82D0-295F6522B8C9")
//                                .queryParam("key", "CD8E487D-2A80-3EB0-A4BC-93E0BEE3696B")
                                .queryParam("service", "address")
                                .queryParam("address", URLEncoder.encode(address, StandardCharsets.UTF_8.toString()))
                                .queryParam("request", "getCoord")
                                .queryParam("version", "1.0.0")
                                .queryParam("type", type)
                                .build();
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        JsonNode responseNode = jsonNode.path("response");
        JsonNode resultNode = responseNode.path("result");
        JsonNode pointNode = resultNode.path("point");
        JsonNode xNode = pointNode.path("x");
        JsonNode yNode = pointNode.path("y");

        HashMap<String, String> result = new HashMap<>();
        result.put("lon", xNode.textValue());
        result.put("lat", yNode.textValue());

        return result;
    }
}
