package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public interface OrderDAO {
    ResponseData registerOrder(String phone, String menuName, String menuCode, List<Long> options, Long price);
    int deleteOrder(String uuid);
    JSONArray getOrders();
    JSONObject getOrder(String uuid);
}
