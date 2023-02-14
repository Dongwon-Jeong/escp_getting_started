package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.Optional;


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
    public String getIdByPhone(String phone) {
        return getDataByPhone(phone, "_uuid");
    }

    @Override
    public String getCellIdByPhone(String phone) {
        return getDataByPhone(phone, "cell");
    }

    @Override
    public String getNameByPhone(String phone) {
        return getDataByPhone(phone, "name");
    }

    private String getDataByPhone(String phone, String data) {
        JSONArray memberList = getMemberList();
        JSONObject member = (JSONObject) memberList.stream()
                .filter(memberObj -> ((JSONObject) memberObj).get("phone").equals(phone))
                .findFirst()
                .orElse(null);
        return (member != null) ? (String) member.get(data) : null;
    }
    @Override
    public JSONArray getMemberList() {
        return commonDAO.getItems(URL);
    }


}
