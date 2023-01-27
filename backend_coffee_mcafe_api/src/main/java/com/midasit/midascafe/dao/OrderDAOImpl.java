package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

import java.net.HttpURLConnection;
import java.util.List;

@Repository
@RequiredArgsConstructor  // 생성자 주입
public class OrderDAOImpl implements OrderDAO {
    private final CommonDAO commonDAO;
    private final static String URL = "https://crudapi.co.uk/api/v1/order";

    @Override
    public ResponseData registerOrder(String phone, String menuName, String menuCode, List<Long> options, Long price) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("phone", phone);
        data.put("menuName", menuName);
        data.put("menuCode", menuCode);
        data.put("options", options);
        data.put("price", price);
        body.add(data);
        ResponseData postResponse = commonDAO.postRequest(body, URL);
        return postResponse;
    }

    @Override
    public int deleteOrder(String uuid) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("_uuid", uuid);
        body.add(data);

        HttpURLConnection connection = commonDAO.getConnection(URL, "DELETE");
        return commonDAO.getResponseCode(connection, body.toString());
    }

    @Override
    public JSONArray getOrders() {
        return commonDAO.getItems(URL);
    }

    @Override
    public JSONObject getOrder(String uuid) {
        return commonDAO.getItem(URL + "/" + uuid);
    }

}
