package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterMemberRq;
import com.midasit.midascafe.dao.GroupDAO;
import com.midasit.midascafe.dao.MemberDAO;
import com.midasit.midascafe.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor  // 생성자 주입
public class MemberServiceImpl implements MemberService{
    private final MemberDAO memberDAO;
    private final GroupDAO groupDAO;
    private final OrderService orderService;
    @Override
    public int registerMember(RegisterMemberRq registerMemberRq) {
        String phone = registerMemberRq.getPhone();
        String name = registerMemberRq.getName();
        String group = registerMemberRq.getGroup();


        JSONArray items = memberDAO.getMemberList();
        for (Object item : items) {
            String mPhone = (String) ((JSONObject) item).get("phone");
            if(mPhone.equals(phone)) {
                return 409;
            }
        }

        String groupId = null;
        items = groupDAO.getGroupList();
        for (Object item : items) {
            String groupName = (String) ((JSONObject) item).get("name");
            if (groupName.equals(group)) {
                groupId = (String) ((JSONObject) item).get("_uuid");
            }
        }
        if(groupId == null) {
            return 404;
        }

        ResponseData postResponse = memberDAO.registerMember(phone, name, groupId);
        JSONParser parser = new JSONParser();
        JSONObject responseJson;
        try {
            responseJson = (JSONObject) parser.parse(postResponse.getResponseData());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String uuid = (String) ((JSONObject) ((JSONArray) responseJson.get("items")).get(0)).get("_uuid");
        groupDAO.addMember(groupId, uuid);

        return postResponse.getStatusCode();
    }

    @Override
    public int deleteMember(String phone) {
        String memberId = memberDAO.getIdByPhone(phone);
        if (memberId == null) {
            return 404;
        }
        orderService.deleteOrder(phone);
        JSONObject member = memberDAO.getMemberById(memberId);
        String groupId = (String) member.get("group");
        groupDAO.deleteMember(groupId, memberId);
        return memberDAO.deleteMember(memberId);
    }


}
