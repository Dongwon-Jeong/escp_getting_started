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
public class GroupDAOImpl implements GroupDAO {
    private final CommonDAO commonDAO;
    private final MemberDAO memberDAO;
    private final static String URL = "https://crudapi.co.uk/api/v1/group";

    @Override
    public int registerGroup(String name) {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("name", name);
        body.add(data);
        return commonDAO.postRequest(body, URL).getStatusCode();
    }

    @Override
    public List<String> findAll() {
        List<String> groupList = new ArrayList<>();
        JSONArray items = getGroupList();
        for (Object item : items) {
            groupList.add((String) ((JSONObject) item).get("name"));
        }
        return groupList;

    }


    @Override
    public JSONArray findMemberByGroup(String group) {
        JSONArray groupList = getGroupList();
        for (Object groupObj : groupList) {
            if (group.equals(((JSONObject) groupObj).get("name"))) {
                JSONArray memberArray = new JSONArray();
                JSONArray memberIdArray = (JSONArray) ((JSONObject) groupObj).get("member");
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
    public int modifyGroupName(String uuid, String name) {
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
        JSONObject group = commonDAO.getItem(URL + "/" + uuid);
        JSONArray memberArr = (JSONArray) group.get("member");
        if (memberArr == null) {
            memberArr = new JSONArray();
        }
        memberArr.add(memberId);
        group.put("member", memberArr);
        HttpURLConnection connection = commonDAO.getConnection(URL + "/" + uuid, "PUT");
        return commonDAO.getResponseCode(connection, group.toString());
    }

    @Override
    public int deleteMember(String uuid, String memberId) {
        JSONObject group = commonDAO.getItem(URL + "/" + uuid);
        JSONArray memberArr = (JSONArray) group.get("member");
        if (memberArr == null) {
            memberArr = new JSONArray();
        }
        memberArr.remove(memberId);
        group.put("member", memberArr);
        HttpURLConnection connection = commonDAO.getConnection(URL + "/" + uuid, "PUT");
        return commonDAO.getResponseCode(connection, group.toString());
    }

    @Override
    public JSONArray getGroupList() {
        return commonDAO.getItems(URL);
    }

    @Override
    public int deleteGroup(String uuid) {
        return commonDAO.deleteItem(URL, uuid);
    }

    @Override
    public String getGroupIdByName(String name) {
        JSONArray groupList = getGroupList();
        JSONObject group = (JSONObject) groupList.stream()
                .filter(groupObj -> ((JSONObject) groupObj).get("name").equals(name))
                .findFirst()
                .orElse(null);
        return (group != null) ? (String) group.get("_uuid") : null;
    }
}
