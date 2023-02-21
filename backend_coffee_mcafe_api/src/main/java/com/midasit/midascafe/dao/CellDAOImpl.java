package com.midasit.midascafe.dao;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor  // 생성자 주입
public class CellDAOImpl implements CellDAO{
    private final CommonDAO commonDAO;
    private final MemberDAO memberDAO;
    private final static String URL = "https://crudapi.co.uk/api/v1/cell";

    @Override
    public int registerCell(String name) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("name", name);
        body.add(data);
        return commonDAO.postRequest(body, URL).getStatusCode();
    }

    @Override
    public List<String> findAll() {
        List<String> cellList = new ArrayList<>();
        JSONArray items = getCellList();
        for (Object item : items) {
            cellList.add((String) ((JSONObject) item).get("name"));
        }
        return cellList;

    }


    @Override
    public JSONArray findMemberByCell(String cell) {
        JSONArray cellList = getCellList();
        for (Object cellObj : cellList) {
            if (cell.equals(((JSONObject) cellObj).get("name"))) {
                JSONArray memberArray = new JSONArray();
                JSONArray memberIdArray = (JSONArray) ((JSONObject) cellObj).get("member");
                if (memberIdArray != null) {
                    for (Object memberId : memberIdArray) {
                        JSONObject memberJson = memberDAO.getMemberById((String) memberId);
                        JSONObject member = new JSONObject();
                        String phone = ((String) memberJson.get("phone"));
                        member.put("phone", phone.substring(phone.length() - 4));
                        member.put("name", memberJson.get("name"));

                        memberArray.add(member);
                    }
                }
                return memberArray;
            }
        }
        return null;
    }

    @Override
    public int modifyCellName(String uuid, String name) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("_uuid", uuid);
        data.put("name", name);
        body.add(data);

        HttpURLConnection connection = commonDAO.getConnection(URL, "PUT");
        return commonDAO.getResponseCode(connection, body.toString());
    }


    @Override
    public int addMember(String uuid, String memberId) {
        JSONObject cell = commonDAO.getItem(URL + "/" + uuid);
        JSONArray memberArr = (JSONArray) cell.get("member");
        if (memberArr == null) {
            memberArr = new JSONArray();
        }
        memberArr.add(memberId);
        cell.put("member", memberArr);
        HttpURLConnection connection = commonDAO.getConnection(URL + "/" + uuid, "PUT");
        return commonDAO.getResponseCode(connection, cell.toString());
    }

    @Override
    public int deleteMember(String uuid, String memberId) {
        JSONObject cell = commonDAO.getItem(URL + "/" + uuid);
        JSONArray memberArr = (JSONArray) cell.get("member");
        if (memberArr == null) {
            memberArr = new JSONArray();
        }
        memberArr.remove(memberId);
        cell.put("member", memberArr);
        HttpURLConnection connection = commonDAO.getConnection(URL + "/" + uuid, "PUT");
        return commonDAO.getResponseCode(connection, cell.toString());
    }

    @Override
    public JSONArray getCellList() {
        return commonDAO.getItems(URL);
    }

    @Override
    public int deleteCell(String uuid) {
        return commonDAO.deleteItem(URL, uuid);
    }

    @Override
    public String getCellIdByName(String name) {
        JSONArray cellList = getCellList();
        JSONObject cell = (JSONObject) cellList.stream()
                .filter(cellObj -> ((JSONObject) cellObj).get("name").equals(name))
                .findFirst()
                .orElse(null);
        return (cell != null) ? (String) cell.get("_uuid") : null;
    }
}
