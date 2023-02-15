package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor  // 생성자 주입
public class OrderDAOImpl implements OrderDAO {
    private final CommonDAO commonDAO;
    private final static String URL = "https://crudapi.co.uk/api/v1/order";

    @Override
    public ResponseData registerOrder(String memberId, String cellId, String menuId, List<Integer> optionValueIdList) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("memberId", memberId);
        data.put("cellId", cellId);
        data.put("menuId", menuId);
        data.put("optionValueIdList", optionValueIdList);
        body.add(data);
        return commonDAO.postRequest(body, URL);
    }

    @Override
    public int deleteOrder(String uuid) {
        return commonDAO.deleteItem(URL, uuid);
    }

    @Override
    public JSONArray getOrderList() {
        return commonDAO.getItems(URL);
    }

    @Override
    public JSONObject getOrder(String uuid) {
        return commonDAO.getItem(URL + "/" + uuid);
    }

}
