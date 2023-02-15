package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor  // 생성자 주입
public class OptionValueDAOImpl implements OptionValueDAO {
    private final CommonDAO commonDAO;
    private final static String URL = "https://crudapi.co.uk/api/v1/optionValue";

    @Override
    public ResponseData registerOptionValue(int code, String name, int price) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();

        data.put("name", name);
        data.put("code", code);
        data.put("price", price);

        body.add(data);

        return commonDAO.postRequest(body, URL);
    }

    @Override
    public JSONObject getOptionValueById(String optionValueId) {
        return commonDAO.getItem(URL + "/" + optionValueId);
    }

    @Override
    public int deleteOptionValue(String valueId) {
        return commonDAO.deleteItem(URL, valueId);
    }
}
