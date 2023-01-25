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
        JSONArray items = getCells();
        for (Object item : items) {
            cellList.add((String) ((JSONObject) item).get("name"));
        }
        return cellList;

    }


    @Override
    public JSONArray findMemberByCell(String cell) {
        JSONArray items = getCells();
        for (Object item : items) {
            if (cell.equals(((JSONObject) item).get("name"))) {
                JSONArray memberArray = new JSONArray();
                JSONArray memberIdArray = (JSONArray) ((JSONObject) item).get("member");
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
    public int addMember(String uuid, String member) {
        JSONObject cell = commonDAO.getItem(URL + "/" + uuid);
        JSONArray memberArr = (JSONArray) cell.get("member");
        if (memberArr == null) {
            memberArr = new JSONArray();
        }
        memberArr.add(member);
        cell.put("member", memberArr);
        HttpURLConnection connection = commonDAO.getConnection(URL + "/" + uuid, "PUT");
        return commonDAO.getResponseCode(connection, cell.toString());
    }

    @Override
    public int addOrder(String uuid, String order) {
        JSONObject cell = commonDAO.getItem(URL + "/" + uuid);
        JSONArray orderArr = (JSONArray) cell.get("order");
        if (orderArr == null) {
            orderArr = new JSONArray();
        }
        orderArr.add(order);
        cell.put("order", orderArr);
        HttpURLConnection connection = commonDAO.getConnection(URL + "/" + uuid, "PUT");
        return commonDAO.getResponseCode(connection, cell.toString());
    }

    @Override
    public int deleteOrder(String uuid, String order) {
        JSONObject cell = commonDAO.getItem(URL + "/" + uuid);
        JSONArray orderArr = (JSONArray) cell.get("order");
        if (orderArr == null) {
            orderArr = new JSONArray();
        }
        orderArr.remove(order);
        cell.put("order", orderArr);
        HttpURLConnection connection = commonDAO.getConnection(URL + "/" + uuid, "PUT");
        return commonDAO.getResponseCode(connection, cell.toString());
    }

    @Override
    public JSONArray getCells() {
        return commonDAO.getItems(URL);
    }

    @Override
    public int deleteCell(String uuid) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("_uuid", uuid);
        body.add(data);

        HttpURLConnection connection = commonDAO.getConnection(URL, "DELETE");
        return commonDAO.getResponseCode(connection, body.toString());
    }

    @Override
    public List<String> getOrderIdListByName(String cell) {
        JSONArray cellArr = getCells();
        for (Object cellObject : cellArr) {
            String cellObjectName = (String) ((JSONObject) cellObject).get("name");
            if (cellObjectName.equals(cell)) {
                return (JSONArray) ((JSONObject) cellObject).get("order");
            }
        }
        return null;
    }

}
