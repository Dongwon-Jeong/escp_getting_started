package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.ModifyGroupNameRq;
import com.midasit.midascafe.controller.rqrs.RegisterGroupRq;
import com.midasit.midascafe.dao.GroupDAO;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor  // 생성자 주입
public class GroupServiceImpl implements GroupService {
    private final GroupDAO groupDAO;
    @Override
    public int registerGroup(RegisterGroupRq registerGroupRq) {
        String name = registerGroupRq.getName();
        List<String> groupList = groupDAO.findAll();
        if (groupList.contains(name)) {
            return 409;
        }

        return groupDAO.registerGroup(name);
    }

    @Override
    public List<String> getGroupList() {
        return groupDAO.findAll();
    }

    @Override
    public JSONArray getMemberList(String group) {
        return groupDAO.findMemberByGroup(group);
    }

    @Override
    public int modifyGroupName(String name, ModifyGroupNameRq modifyGroupNameRq) {
        JSONArray items = groupDAO.getGroupList();
        String uuid = null;
        boolean duplicate = false;
        for (Object item : items) {
            String groupName = (String) ((JSONObject) item).get("name");
            if (groupName.equals(modifyGroupNameRq.getName())) {
                duplicate = true;
            }
            if (groupName.equals(name)) {
                uuid = (String) ((JSONObject) item).get("_uuid");
            }
        }
        if (uuid == null) { return 404; }
        if (duplicate) { return 409; }

        return groupDAO.modifyGroupName(uuid, modifyGroupNameRq.getName());
    }

    @Override
    public int deleteGroup(String name) {
        JSONArray items = groupDAO.getGroupList();
        String uuid = null;
        boolean hasMember = false;
        for (Object item : items) {
            String groupName = (String) ((JSONObject) item).get("name");
            if (groupName.equals(name)) {
                uuid = (String) ((JSONObject) item).get("_uuid");
                if(((JSONObject) item).get("member") != null) {
                    hasMember = true;
                }
                break;
            }
        }
        if (uuid == null) { return 404; }
        if (hasMember) { return 403; }
        return groupDAO.deleteGroup(uuid);
    }
}