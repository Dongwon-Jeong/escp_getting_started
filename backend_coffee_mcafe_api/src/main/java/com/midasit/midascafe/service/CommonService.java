package com.midasit.midascafe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public interface CommonService {
    int getProjectSeq();

    void updateProjectSeq();

    String getApiKey();

    WebClient getDbClient();

    WebClient getUchefClient();

    ObjectMapper getObjectMapper();

    List<String> parseAuthorizationValue(String authorizationValue);

}
