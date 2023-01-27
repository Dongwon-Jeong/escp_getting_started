package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.HttpURLConnection;

public interface CommonDAO {
    HttpURLConnection getConnection(String URL, String method);
    ResponseData postRequest(JSONArray body, String url);
    JSONArray getItems(String url);

    JSONObject getItem(String url);

    int getResponseCode(HttpURLConnection connection, String body);
}
