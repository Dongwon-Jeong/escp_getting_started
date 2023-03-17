package com.midasit.midascafe.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.midasit.midascafe.dto.Group;
import com.midasit.midascafe.dto.Member;
import org.json.simple.JSONArray;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public interface GroupDAO {
    HttpStatus registerGroup(String name);

    JSONArray findMemberByGroup(String group);

    int modifyGroupName(String uuid, String name);

    List<Group> getGroupList();

    Map<String, String> getGroupMap();

    public HttpStatus modifyGroup(String uri, JsonNode body);

    int deleteGroup(String uuid);
    String getGroupIdByName(String group);
}
