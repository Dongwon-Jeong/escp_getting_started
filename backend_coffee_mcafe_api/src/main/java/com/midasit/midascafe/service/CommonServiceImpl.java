package com.midasit.midascafe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService{
    @Value("${database.api.key}")
    private String API_KEY;
    private static WebClient dbClient;
    private static WebClient uchefClient;
    private ObjectMapper objectMapper;
    private int projectSeq = -1;

    @Override
    public int getProjectSeq() {
        if (projectSeq == -1) {
            updateProjectSeq();
        }
        return projectSeq;
    }

    @Override
    public void updateProjectSeq() {
        WebClient uchefClient = getUchefClient();
        Mono<String> responseMono = uchefClient.get()
                .uri("/webApp.action?shop_member_seq=1859&mode=5150")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
        String responseStr = responseMono.block();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson;
        try {
            responseJson = objectMapper.readTree(responseStr);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        projectSeq = responseJson.get("searchResult").get("memberData").get("default_project_seq").asInt();
    }

    @Override
    public String getApiKey() {
        return API_KEY;
    }

    @Override
    public WebClient getDbClient() {
        if (dbClient == null) {
            dbClient = WebClient.builder()
                    .baseUrl("https://crudapi.co.uk/api/v1")
                    .defaultHeader(HttpHeaders.ACCEPT, "*/*")
                    .defaultHeader(HttpHeaders.AUTHORIZATION, API_KEY)
                    .build();
        }
        return dbClient;
    }

    @Override
    public WebClient getUchefClient() {
        if (uchefClient == null) {
            uchefClient = WebClient.builder()
                    .baseUrl("https://uchef.co.kr/")
                    .defaultHeader(HttpHeaders.ACCEPT, "*/*")
                    .build();
        }
        return uchefClient;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return objectMapper;
    }

    @Override
    public List<String> parseAuthorizationValue(String authorizationValue) {
        List<String> valueParsed = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(authorizationValue);
        valueParsed.add(st.nextToken());
        String encodedValue = st.nextToken();
        String decodedValue = new String(Base64.getDecoder().decode(encodedValue));
        st = new StringTokenizer(decodedValue,":");
        valueParsed.add(st.nextToken());
        valueParsed.add(st.nextToken());
        return valueParsed;
    }
}
