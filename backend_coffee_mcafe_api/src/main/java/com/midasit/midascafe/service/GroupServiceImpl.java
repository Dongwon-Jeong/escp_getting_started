package com.midasit.midascafe.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.midasit.midascafe.controller.rqrs.ModifyGroupNameRq;
import com.midasit.midascafe.controller.rqrs.RegisterGroupRq;
import com.midasit.midascafe.dao.CommonDAO;
import com.midasit.midascafe.dao.GroupDAO;
import com.midasit.midascafe.dto.Group;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor  // 생성자 주입
public class GroupServiceImpl implements GroupService {
    private final CommonDAO commonDAO;
    private final GroupDAO groupDAO;
    private final CommonService commonService;
    private final String URI = "/group";
    @Override
    public HttpStatus registerGroup(RegisterGroupRq registerGroupRq) {
        String name = registerGroupRq.getName();
        Map<String, String> groupList = getGroupMap();
        if (groupList.containsKey(name)) {
            return HttpStatus.CONFLICT;
        }
        return groupDAO.registerGroup(name);
    }

    @Override
    public Map<String, String> getGroupMap() {
        return groupDAO.getGroupMap();
    }

    @Override
    public boolean areExistIds(List<String> groupIdList) {
        Map<String, String> groupMap = getGroupMap();
        return groupIdList.stream()
                .filter(x -> !groupMap.containsValue(x))
                .findFirst()
                .isEmpty();
    }

    @Override
    public JSONArray getMemberList(String group) {
        return groupDAO.findMemberByGroup(group);
    }

    @Override
    public int modifyGroupName(String name, ModifyGroupNameRq modifyGroupNameRq) {
//        JSONArray items = groupDAO.getGroupMap();
//        String uuid = null;
//        boolean duplicate = false;
//        for (Object item : items) {
//            String groupName = (String) ((JSONObject) item).get("name");
//            if (groupName.equals(modifyGroupNameRq.getName())) {
//                duplicate = true;
//            }
//            if (groupName.equals(name)) {
//                uuid = (String) ((JSONObject) item).get("_uuid");
//            }
//        }
//        if (uuid == null) { return 404; }
//        if (duplicate) { return 409; }
//
//        return groupDAO.modifyGroupName(uuid, modifyGroupNameRq.getName());
        return 0;
    }

    @Override
    public int deleteGroup(String name) {
//        JSONArray items = groupDAO.getGroupMap();
//        String uuid = null;
//        boolean hasMember = false;
//        for (Object item : items) {
//            String groupName = (String) ((JSONObject) item).get("name");
//            if (groupName.equals(name)) {
//                uuid = (String) ((JSONObject) item).get("_uuid");
//                if(((JSONObject) item).get("member") != null) {
//                    hasMember = true;
//                }
//                break;
//            }
//        }
//        if (uuid == null) { return 404; }
//        if (hasMember) { return 403; }
//        return groupDAO.deleteGroup(uuid);
        return 0;
    }

    @Override
    public HttpStatus addOrderId(String groupId, String orderId) {
        String uri = new StringBuilder(URI).append("/").append(groupId).toString();
        ResponseEntity<JsonNode> groupResponseEntity = commonDAO.getRequest(uri, JsonNode.class).block();
        if (groupResponseEntity.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return HttpStatus.BAD_REQUEST;
        }
        JsonNode orderIdListJson = groupResponseEntity.getBody().get("orderIdList");
        List<String> orderIdList = new ArrayList<>();
        orderIdListJson.forEach(x -> orderIdList.add(x.textValue()));
        orderIdList.add(orderId);
        Map<String, Object> body = new HashMap<>();
        body.put("orderIdList", orderIdList);
        return groupDAO.modifyGroup(uri, commonService.getObjectMapper().valueToTree(body));
    }

    @Override
    public HttpStatus removeOrderId(String groupId, List<String> orderIdList) {
        String uri = new StringBuilder(URI).append("/").append(groupId).toString();
        Map<String, Object> body = new HashMap<>();
        body.put("orderIdList", orderIdList);
        return groupDAO.modifyGroup(uri, commonService.getObjectMapper().valueToTree(body));
    }

    @Override
    public HttpStatus removeOrderId(List<String> orderIdListForDelete) {
        List<Group> groupList = groupDAO.getGroupList();
        for (String orderId : orderIdListForDelete) {
            Optional<Group> groupOptional = groupList.stream()
                    .filter(group -> group.getOrderIdList().contains(orderId))
                    .findFirst();
            if (groupOptional.isPresent()) {
                Group group = groupOptional.get();
                String groupId = group.get_uuid();
                List<String> orderIdList = group.getOrderIdList();
                orderIdList.remove(orderId);
                removeOrderId(groupId, orderIdList);
            }
        }
        return HttpStatus.OK;
    }
}