package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor  // 생성자 주입
public class OptionDAOImpl implements OptionDAO{

    private final CommonDAO commonDAO;
    private final static String URL = "https://crudapi.co.uk/api/v1/option";

    @Override
    public ResponseData registerOption(List<String> name, List<Integer> code, List<Integer> price, boolean essential, String description) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();

        data.put("name", name);
        data.put("code", code);
        if (price != null) {
            data.put("price", price);
        }
        data.put("essential", essential);
        if (description != null) {
            data.put("description", description);
        }
        body.add(data);

        return commonDAO.postRequest(body, URL);
    }

    @Override
    public JSONObject getOptionById(String uuid) {
        return commonDAO.getItem(URL + "/" + uuid);
    }
}
