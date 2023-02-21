package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterOrderRq;
import com.midasit.midascafe.dao.*;
import com.midasit.midascafe.dto.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderDAO orderDAO;
    private final MemberDAO memberDAO;
    private final GroupDAO groupDAO;
    private final MenuService menuService;
    @Override
    public int registerOrder(RegisterOrderRq registerOrderRq) {
        String phone = registerOrderRq.getPhone();
        if (hasOrder(phone)) { return 409; }
        String memberId = memberDAO.getIdByPhone(phone);
        String groupId = memberDAO.getGroupIdByPhone(phone);
        if(groupId == null) { return 404; }
        String menuCode = registerOrderRq.getMenuCode();
        List<Integer> optionValueList = registerOrderRq.getOptionValueList();

        boolean setDefault = registerOrderRq.getSetDefault();
        if (setDefault) {
            // TODO: 기본 옵션 처리
        }

        ResponseData postResponse = orderDAO.registerOrder(memberId, groupId, menuCode, optionValueList);

        return postResponse.getStatusCode();
    }

    @Override
    public int deleteOrder(String phone) {
        JSONArray orders = orderDAO.getOrderList();
        for (Object orderObject : orders) {
            String orderMemberId = (String) ((JSONObject) orderObject).get("memberId");
            String memberId = memberDAO.getIdByPhone(phone);
            if(orderMemberId.equals(memberId)) {
                String uuid = (String) ((JSONObject) orderObject).get("_uuid");
                return orderDAO.deleteOrder(uuid);
            }
        }
        return 404;
    }

    @Override
    public boolean hasOrder(String phone) {
        JSONArray orderList = orderDAO.getOrderList();
        String memberId = memberDAO.getIdByPhone(phone);
        for (Object order : orderList) {
            String orderMemberId = (String) ((JSONObject) order).get("memberId");
            if (orderMemberId.equals(memberId)) { return true; }
        }
        return false;
    }

    @Override
    public List<Order> getOrderList(String groupName) {
        // TODO:
        String groupId = groupDAO.getGroupIdByName(groupName);
        JSONArray orderListJson = orderDAO.getOrderList();
        JSONArray memberList = memberDAO.getMemberList();
        List<Order> orderList = new ArrayList<>();
        for (Object orderObj : orderListJson) {
            JSONObject orderJsonObj = (JSONObject) orderObj;
            if (orderJsonObj.get("groupId").equals(groupId)) {
                MenuDetail menuDetail = menuService.getMenuDetail((String) orderJsonObj.get("menuCode"));
                List<String> optionNameList = new ArrayList<>();
                JSONArray optionValueList = (JSONArray) orderJsonObj.get("optionValueList");
                optionValueList.forEach(optionValue -> optionNameList.add(menuDetail.getOptionValueMap().get(optionValue).getName()));
                JSONObject member = (JSONObject) memberList.stream()
                        .filter(memberObj -> ((JSONObject) memberObj).get("_uuid").equals(orderJsonObj.get("memberId")))
                        .findFirst()
                        .orElse(null);
                String name = (member != null) ? (String) member.get("name") : null;
                orderList.add(Order.builder()
                        .name(name)
                        .menuName(menuDetail.getName())
                        .optionNameList(optionNameList)
                        .build());
            }
        }
        return orderList;
    }

    // 임시 메서드 고도화 때 사라질 예정
    public List<Order> getOrderListByPhone(String phone) {
        String groupId = memberDAO.getGroupIdByPhone(phone);
        // Todo: phone이 존재 하지 않는 회원일 때 처리
        JSONArray orderListJson = orderDAO.getOrderList();
        JSONArray memberList = memberDAO.getMemberList();
        List<Order> orderList = new ArrayList<>();
        for (Object orderObj : orderListJson) {
            JSONObject orderJsonObj = (JSONObject) orderObj;
            if (orderJsonObj.get("groupId").equals(groupId)) {
                MenuDetail menuDetail = menuService.getMenuDetail((String) orderJsonObj.get("menuCode"));
                List<String> optionNameList = new ArrayList<>();
                JSONArray optionValueList = (JSONArray) orderJsonObj.get("optionValueList");
                optionValueList.forEach(optionValue -> optionNameList.add(menuDetail.getOptionValueMap().get(optionValue).getName()));
                JSONObject member = (JSONObject) memberList.stream()
                        .filter(memberObj -> ((JSONObject) memberObj).get("_uuid").equals(orderJsonObj.get("memberId")))
                        .findFirst()
                        .orElse(null);
                String name = (member != null) ? (String) member.get("name") : null;
                orderList.add(Order.builder()
                        .name(name)
                        .menuName(menuDetail.getName())
                        .optionNameList(optionNameList)
                        .build());
            }
        }
        return orderList;
    }
}
