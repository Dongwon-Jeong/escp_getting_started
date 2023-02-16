package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public interface OrderDAO {
    ResponseData registerOrder(String memberId, String cellId, String menuCode, List<Integer> optionValueList);
    int deleteOrder(String uuid);
    JSONArray getOrderList();
    JSONObject getOrder(String uuid);
}
