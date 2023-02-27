package com.midasit.midascafe.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.midasit.midascafe.dto.ResponseData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public interface CommonDAO {
    <T> Mono<ResponseEntity<T>> postRequest(String uri, JsonNode body, Class<T> classType);

    <T> Mono<ResponseEntity<T>> getRequest(String uri, Class<T> classType);

    <T> Mono<ResponseEntity<T>> putRequest(String uri, JsonNode body, Class<T> classType);

    <T> Mono<ResponseEntity<T>> deleteRequest(String uri, Class<T> classType);

    <T> Mono<ResponseEntity<T>> deleteRequestWithBody(String uri, JsonNode body, Class<T> classType);

    <T> List<T> getItemList(String uri, Class<T> classType);

    Map<String, String> getItemMap(String URI, String key, String value);

    HttpURLConnection getConnection(String URL, String method);

    JSONObject getItem(String url);

    int deleteItem(String url, String uuid);

    int deleteItems(String url, JSONArray orderJsonArray);

    int getResponseCode(HttpURLConnection connection, String body);

}
