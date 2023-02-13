package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterOrderRq;
import com.midasit.midascafe.dao.*;
import com.midasit.midascafe.dto.OptionGroup;
import com.midasit.midascafe.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderDAO orderDAO;
    private final MenuDAO menuDAO;
    private final MemberDAO memberDAO;
    @Override
    public int registerOrder(RegisterOrderRq registerOrderRq) {
        String phone = registerOrderRq.getPhone();
        if (hasOrder(phone)) { return 409; }
        String memberId = memberDAO.getIdByPhone(phone);
        String cellId = memberDAO.getCellIdByPhone(phone);
        if(cellId == null) { return 404; }
        String menuCode = registerOrderRq.getMenuCode();
        JSONObject menu = menuDAO.getMenuByCode(menuCode);
        String menuId = (String) menu.get("_uuid");
        List<Integer> optionValueList = registerOrderRq.getOptionValueList();


        boolean setDefault = registerOrderRq.getSetDefault();
        if (setDefault) {
            // TODO: 기본 옵션 처리
        }

        ResponseData postResponse = orderDAO.registerOrder(memberId, cellId, menuId, optionValueList);

        return postResponse.getStatusCode();
    }

    @Override
    public int deleteOrder(String phone) {
        JSONArray orders = orderDAO.getOrders();
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
        JSONArray orderArr = orderDAO.getOrders();
        String memberId = memberDAO.getIdByPhone(phone);
        for (Object order : orderArr) {
            String orderMemberId = (String) ((JSONObject) order).get("memberId");
            if (orderMemberId.equals(memberId)) { return true; }
        }
        return false;
    }
}
