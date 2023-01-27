package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor  // 생성자 주입
public class MemberDAOImpl implements MemberDAO{

    private final CommonDAO commonDAO;
    private final static String URL = "https://crudapi.co.uk/api/v1/member";
    @Override
    public ResponseData registerMember(String phone, String name, String cellId) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("phone", phone);
        data.put("name", name);
        data.put("cell", cellId);
        body.add(data);
        ResponseData postResponse = commonDAO.postRequest(body, URL);
        return postResponse;
    }

    @Override
    public JSONObject getMemberById(String uuid) {
        JSONObject member;
        member = commonDAO.getItem(URL + "/" + uuid);
        return member;
    }

    @Override
    public String getCellIdByPhone(String phone) {
        JSONArray items = getMembers();
        for (Object item : items) {
            String itemPhone = (String) ((JSONObject) item).get("phone");
            if (itemPhone.equals(phone)) {
                return (String) ((JSONObject) item).get("cell");
            }
        }
        return null;
    }

    @Override
    public String getNameByPhone(String phone) {
        JSONArray items = getMembers();
        for (Object item : items) {
            String itemPhone = (String) ((JSONObject) item).get("phone");
            if (itemPhone.equals(phone)) {
                return (String) ((JSONObject) item).get("name");
            }
        }
        return null;
    }

    @Override
    public JSONArray getMembers() {
        return commonDAO.getItems(URL);
    }


}
