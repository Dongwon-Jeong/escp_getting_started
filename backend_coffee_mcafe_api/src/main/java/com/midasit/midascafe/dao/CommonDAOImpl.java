package com.midasit.midascafe.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.midasit.midascafe.dto.ResponseData;
import com.midasit.midascafe.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor  // 생성자 주입
public class CommonDAOImpl implements CommonDAO{
    private final CommonService commonService;
    @Override
    public <T> Mono<ResponseEntity<T>> postRequest(String uri, JsonNode body, Class<T> classType) {
        return commonService.getDbClient().post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body.toString())
                .retrieve()
                .toEntity(classType);
    }

    @Override
    public <T> Mono<ResponseEntity<T>> getRequest(String uri, Class<T> classType) {
        return commonService.getDbClient().get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(classType);
    }

    @Override
    public <T> Mono<ResponseEntity<T>> putRequest(String uri, JsonNode body, Class<T> classType) {
        return commonService.getDbClient().put()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body.toString())
                .retrieve()
                .toEntity(classType);
    }

    @Override
    public <T> Mono<ResponseEntity<T>> deleteRequest(String uri, Class<T> classType) {
        return commonService.getDbClient().delete()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(classType);
    }

    @Override
    public <T> Mono<ResponseEntity<T>> deleteRequestWithBody(String uri, JsonNode body, Class<T> classType) {
        return commonService.getDbClient().method(HttpMethod.DELETE)
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toEntity(classType);
    }

    @Override
    public <T> List<T> getItemList(String uri, Class<T> classType) {
        Mono<ResponseEntity<JsonNode>> itemListMono = getRequest(uri, JsonNode.class);
        JsonNode itemListJson =  itemListMono.block().getBody().get("items");
        ObjectMapper objectMapper = commonService.getObjectMapper();
        List<T> itemList = new ArrayList<>();
        itemListJson.forEach(x -> {
            try {
                itemList.add(objectMapper.treeToValue(x, classType));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return itemList;
    }

    @Override
    public Map<String, String> getItemMap(String URI, String key, String value) {
        Map<String, String> itemMap = new HashMap<>();
        Mono<ResponseEntity<String>> groupMono = getRequest(URI, String.class);
        ObjectMapper objectMapper = commonService.getObjectMapper();
        String groupString = groupMono.block().getBody();
        JsonNode groupJson;
        try {
            groupJson = objectMapper.readTree(groupString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        groupJson = groupJson.get("items");
        for (JsonNode groupItem : groupJson) {
            itemMap.put(groupItem.get(key).textValue(), groupItem.get(value).textValue());
        }
        return itemMap;
    }

    @Override
    public HttpURLConnection getConnection(String URL, String method) {
        HttpURLConnection connection;
        try {
            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Authorization", commonService.getApiKey());
            connection.setRequestProperty("Accept", "*/*");
            connection.setDoOutput(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    @Override
    public JSONObject getItem(String url) {
        JSONObject item;
        try {
            HttpURLConnection connection = getConnection(url, "GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }

            JSONParser parser = new JSONParser();
            item = (JSONObject) parser.parse(sb.toString());

            connection.disconnect();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return null;
        }
        return item;
    }

    @Override
    public int deleteItem(String url, String uuid) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("_uuid", uuid);
        body.add(data);

        HttpURLConnection connection = getConnection(url, "DELETE");
        return getResponseCode(connection, body.toString());
    }

    @Override
    public int deleteItems(String url, JSONArray orderJsonArray) {
        HttpURLConnection connection = getConnection(url, "DELETE");
        return getResponseCode(connection, orderJsonArray.toString());
    }

    public int getResponseCode(HttpURLConnection connection, String data) {
        int responseCode;
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(data);
            bw.flush();
            bw.close();
            responseCode = connection.getResponseCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        connection.disconnect();

        return responseCode;
    }
}
