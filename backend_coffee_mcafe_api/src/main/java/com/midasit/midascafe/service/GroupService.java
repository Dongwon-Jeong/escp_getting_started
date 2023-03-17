package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.ModifyGroupNameRq;
import com.midasit.midascafe.controller.rqrs.RegisterGroupRq;
import org.json.simple.JSONArray;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public interface GroupService {

    HttpStatus registerGroup(RegisterGroupRq registerGroupRq);

    Map<String, String> getGroupMap();

    boolean areExistIds(List<String> groupIdList);

    JSONArray getMemberList(String group);

    int modifyGroupName(String name, ModifyGroupNameRq modifyGroupNameRq);

    int deleteGroup(String name);

    HttpStatus addOrderId(String groupId, String orderId);

    HttpStatus removeOrderId(String uuid, List<String> orderIdList);

    HttpStatus removeOrderId(List<String> orderIdListForDelete);

}
