package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.HttpURLConnection;

public interface CommonDAO {
    WebClient getWebClient();
    HttpURLConnection getConnection(String URL, String method);
    ResponseData postRequest(JSONArray body, String uri);
    JSONArray getItems(String url);
    JSONObject getItem(String url);
    int deleteItem(String url, String uuid);
    int getResponseCode(HttpURLConnection connection, String body);
}
