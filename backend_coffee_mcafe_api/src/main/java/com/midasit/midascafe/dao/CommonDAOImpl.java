package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Repository
public class CommonDAOImpl implements CommonDAO{
    @Value("${database.api.key}")
    private String API_KEY;

    private static WebClient webClient;

    @Override
    public WebClient getWebClient() {
        if (webClient == null) {
            webClient = WebClient.builder()
                    .baseUrl("https://crudapi.co.uk/api/v1")
                    .defaultHeader(HttpHeaders.ACCEPT, "*/*")
                    .defaultHeader(HttpHeaders.AUTHORIZATION, API_KEY)
                    .build();
        }
        return webClient;
    }

    @Override
    public HttpURLConnection getConnection(String URL, String method) {
        HttpURLConnection connection;
        try {
            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Authorization", API_KEY);
            connection.setRequestProperty("Accept", "*/*");
            connection.setDoOutput(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    @Override
    public ResponseData postRequest(JSONArray body, String url) {
        int responseCode;
        String responseData;
        try {
            HttpURLConnection connection = getConnection(url, "POST");

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(body.toString());
            bw.flush();
            bw.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuffer sb = new StringBuffer();
            while((responseData = br.readLine()) != null) {
                sb.append(responseData);
            }
            responseData = sb.toString();
            responseCode = connection.getResponseCode();
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseData
                .builder()
                .statusCode(responseCode)
                .responseData(responseData)
                .build();
    }

    @Override
    public JSONArray getItems(String url) {
        JSONArray items;
        try {
            HttpURLConnection connection = getConnection(url, "GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            if (connection.getResponseCode() >= 400) {
                return null;
            }
            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(sb.toString());
            items = (JSONArray) response.get("items");

            connection.disconnect();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return null;
        }

        return items;
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
