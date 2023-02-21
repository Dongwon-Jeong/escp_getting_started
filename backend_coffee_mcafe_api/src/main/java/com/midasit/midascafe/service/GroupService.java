package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.ModifyGroupNameRq;
import com.midasit.midascafe.controller.rqrs.RegisterGroupRq;
import org.json.simple.JSONArray;

import java.util.List;

public interface GroupService {

    int registerGroup(RegisterGroupRq registerGroupRq);
    List<String> getGroupList();
    JSONArray getMemberList(String group);
    int modifyGroupName(String name, ModifyGroupNameRq modifyGroupNameRq);
    int deleteGroup(String name);
}
