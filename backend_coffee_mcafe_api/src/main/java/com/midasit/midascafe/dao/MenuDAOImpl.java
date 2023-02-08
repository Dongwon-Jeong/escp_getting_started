package com.midasit.midascafe.dao;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor  // 생성자 주입
public class MenuDAOImpl implements MenuDAO{
    private final CommonDAO commonDAO;
    private final static String URL = "https://crudapi.co.uk/api/v1/menu";
    @Override
    public int registerMenu(String name, String code, int unitPrice, List<String> optionGroupIdList) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("name", name);
        data.put("code", code);
        data.put("unitPrice", unitPrice);
        data.put("optionGroupIdList", optionGroupIdList);
        body.add(data);
        return commonDAO.postRequest(body, URL).getStatusCode();
    }


    @Override
    public JSONArray getMenuList() {
        return commonDAO.getItems(URL);
    }

    @Override
    public JSONObject getMenuByCode(String code) {
        JSONArray items = getMenuList();
        for (Object item : items) {
            String itemCode = (String) ((JSONObject) item).get("code");
            if (itemCode.equals(code)) {
                return (JSONObject) item;
            }
        }
        return null;
    }
}
