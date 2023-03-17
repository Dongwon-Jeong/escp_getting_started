package com.midasit.midascafe.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.midasit.midascafe.dto.Group;
import com.midasit.midascafe.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor  // 생성자 주입
public class GroupDAOImpl implements GroupDAO {
    private final CommonDAO commonDAO;
    private final MemberDAO memberDAO;
    private final CommonService commonService;
    private final static String URL = "https://crudapi.co.uk/api/v1/group";
    private final static String URI = "/group";

    @Override
    public HttpStatus registerGroup(String name) {
        List<Map<String, Object>> body = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("orderIdList", new ArrayList<>());
        body.add(data);

        ObjectMapper objectMapper = commonService.getObjectMapper();
        ResponseEntity<String> responseEntity = commonDAO.postRequest(URI, objectMapper.valueToTree(body), String.class).block();

        return responseEntity.getStatusCode();
    }

    @Override
    public JSONArray findMemberByGroup(String group) {
//        JSONArray groupList = getGroupMap();
//        for (Object groupObj : groupList) {
//            if (group.equals(((JSONObject) groupObj).get("name"))) {
//                JSONArray memberArray = new JSONArray();
//                JSONArray memberIdArray = (JSONArray) ((JSONObject) groupObj).get("member");
//                if (memberIdArray != null) {
//                    for (Object memberId : memberIdArray) {
//                        JSONObject memberJson = memberDAO.getMemberById((String) memberId);
//                        JSONObject member = new JSONObject();
//                        String phone = ((String) memberJson.get("phone"));
//                        member.put("phone", phone.substring(phone.length() - 4));
//                        member.put("name", memberJson.get("name"));
//
//                        memberArray.add(member);
//                    }
//                }
//                return memberArray;
//            }
//        }
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
    public List<Group> getGroupList() {
        return commonDAO.getItemList(URI, Group.class);
    }

    @Override
    public Map<String, String> getGroupMap() {
        return commonDAO.getItemMap(URI, "name", "_uuid");
    }

    @Override
    public HttpStatus modifyGroup(String uri, JsonNode body) {
        return commonDAO.putRequest(uri, body, String.class).block().getStatusCode();
    }

    @Override
    public int deleteGroup(String uuid) {
        return commonDAO.deleteItem(URL, uuid);
    }

    @Override
    public String getGroupIdByName(String name) {
//        JSONArray groupList = getGroupMap();
//        JSONObject group = (JSONObject) groupList.stream()
//                .filter(groupObj -> ((JSONObject) groupObj).get("name").equals(name))
//                .findFirst()
//                .orElse(null);
//        return (group != null) ? (String) group.get("_uuid") : null;
        return null;
    }
}
