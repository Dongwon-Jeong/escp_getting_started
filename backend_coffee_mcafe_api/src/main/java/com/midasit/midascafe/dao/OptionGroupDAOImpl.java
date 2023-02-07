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
public class OptionGroupDAOImpl implements OptionGroupDAO {

    private final CommonDAO commonDAO;
    private final static String URL = "https://crudapi.co.uk/api/v1/optionGroup";

    @Override
    public ResponseData registerOptionGroup(String name, boolean essential, String description) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();

        data.put("name", name);
        data.put("essential", essential);
        data.put("optionValueIdList", new JSONArray());
        if (description != null) {
            data.put("description", description);
        }
        body.add(data);

        return commonDAO.postRequest(body, URL);
    }

    @Override
    public JSONObject getOptionGroupById(String optionGroupId) {
        return commonDAO.getItem(URL + "/" + optionGroupId);
    }

    @Override
    public int deleteOptionGroup(String groupId) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("_uuid", groupId);
        body.add(data);

        HttpURLConnection connection = commonDAO.getConnection(URL, "DELETE");
        return commonDAO.getResponseCode(connection, body.toString());
    }

    @Override
    public int addOptionValue(String groupId, String optionValueId) {
        JSONObject optionGroup = getOptionGroupById(groupId);
        JSONArray optionValueIdList = (JSONArray) optionGroup.get("optionValueIdList");
        optionValueIdList.add(optionValueId);
        JSONObject body = new JSONObject();
        body.put("optionValueIdList", optionValueIdList);
        HttpURLConnection connection = commonDAO.getConnection(URL + "/" + groupId, "PUT");
        return commonDAO.getResponseCode(connection, body.toString());
    }

    @Override
    public int deleteOptionValue(String groupId, String optionValueId) {
        JSONObject optionGroup = getOptionGroupById(groupId);
        if (optionGroup == null) {
            return 404;
        }
        JSONArray optionValueIdList = (JSONArray) optionGroup.get("optionValueIdList");
        optionValueIdList.remove(optionValueId);
        JSONObject body = new JSONObject();
        body.put("optionValueIdList", optionValueIdList);
        HttpURLConnection connection = commonDAO.getConnection(URL + "/" + groupId, "PUT");
        return commonDAO.getResponseCode(connection, body.toString());
    }
}
