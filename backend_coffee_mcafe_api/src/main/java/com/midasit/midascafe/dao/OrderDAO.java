package com.midasit.midascafe.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.midasit.midascafe.dto.Order;
import com.midasit.midascafe.dto.ResponseData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderDAO {
    ResponseEntity<String> registerOrder(Order order);

    ResponseEntity<Order> getOrder(String uuid);
    int deleteOrder(JSONArray orderJsonArray);

    HttpStatus deleteOrder(String orderId);

    HttpStatus deleteOrders(JsonNode body);

    List<Order> getOrderList();
}
