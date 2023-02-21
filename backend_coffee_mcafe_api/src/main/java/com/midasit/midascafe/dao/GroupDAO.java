package com.midasit.midascafe.dao;

import org.json.simple.JSONArray;

import java.util.List;

public interface GroupDAO {
    int registerGroup(String name);
    List<String> findAll();
    JSONArray findMemberByGroup(String group);
    int modifyGroupName(String uuid, String name);
    int addMember(String uuid, String memberId);
    int deleteMember(String uuid, String memberId);
    JSONArray getGroupList();
    int deleteGroup(String uuid);

    String getGroupIdByName(String group);
}
