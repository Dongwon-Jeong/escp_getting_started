package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterOrderRq;
import com.midasit.midascafe.dao.*;
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
    private final OptionDAO optionDAO;
    private final CellDAO cellDAO;
    @Override
    public int registerOrder(RegisterOrderRq registerOrderRq) {
        String phone = registerOrderRq.getPhone();
        if (hasOrder(phone)) { return 409; }
        String cellId = memberDAO.getCellIdByPhone(phone);
        if(cellId == null) { return 404; }

        String menu = registerOrderRq.getMenu();
        List<String> optionNames = registerOrderRq.getOption();
        List<Long> optionCodes = new ArrayList<>();

        // 메뉴에 대한 옵션 uuid 리스트
        JSONArray optionIdArr = null;
        String menuCode = null;
        Long price = 0L;
        JSONArray menuArr = menuDAO.getMenu();
        for (Object menuObject : menuArr) {
            String menuName = (String) ((JSONObject) menuObject).get("name");
            if (menuName.equals(menu)) {
                optionIdArr = (JSONArray) ((JSONObject) menuObject).get("option");
                menuCode = (String) ((JSONObject) menuObject).get("code");
                price = (Long) ((JSONObject) menuObject).get("unitPrice");
            }
        }

        // 옵션 이름 -> 옵션 코드 변환
        for (Object optionId : optionIdArr) {
            JSONObject option = optionDAO.getOptionById((String) optionId);
            JSONArray optionNameArr = (JSONArray) option.get("name");
            JSONArray optionCodeArr = (JSONArray) option.get("code");
            JSONArray optionPriceArr = (JSONArray) option.get("price");
            int optionCnt = optionNames.size();
            int matchCnt = 0;
            boolean escape = false;
            for (int i = 0; i < optionNameArr.size(); i++) {
                for (int j = 0; j < optionCnt; j++) {
                    if (optionNameArr.get(i).equals(optionNames.get(j))) {
                        optionCodes.add((Long) optionCodeArr.get(i));
                        if (optionPriceArr != null) {
                            price += (Long) optionPriceArr.get(i);
                        }
                        matchCnt++;
                        if (matchCnt == optionCnt) { escape = true; }
                    }
                    if (escape) { break; }
                }
                if (escape) { break; }
            }
        }
        ResponseData postResponse = orderDAO.registerOrder(phone, menu, menuCode, optionCodes, price);
        JSONParser parser = new JSONParser();
        JSONObject responseJson;
        try {
            responseJson = (JSONObject) parser.parse(postResponse.getResponseData());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String uuid = (String) ((JSONObject) ((JSONArray) responseJson.get("items")).get(0)).get("_uuid");

        cellDAO.addOrder(cellId, uuid);
        return postResponse.getStatusCode();
    }

    @Override
    public int deleteOrder(String phone) {
        JSONArray orders = orderDAO.getOrders();
        for (Object orderObject : orders) {
            String orderPhone = (String) ((JSONObject) orderObject).get("phone");
            if(orderPhone.equals(phone)) {
                String uuid = (String) ((JSONObject) orderObject).get("_uuid");
                String cellId = memberDAO.getCellIdByPhone(phone);
                cellDAO.deleteOrder(cellId, uuid);
                return orderDAO.deleteOrder(uuid);
            }
        }
        return 404;
    }

    @Override
    public boolean hasOrder(String phone) {
        JSONArray orderArr = orderDAO.getOrders();

        for (Object order : orderArr) {
            String orderPhone = (String) ((JSONObject) order).get("phone");
            if (orderPhone.equals(phone)) { return true; }
        }
        return false;
    }

//    public JSONArray getOptionIdArr(String name) {
//        JSONArray menuArr = menuDAO.getMenu();
//        for (Object menu : menuArr) {
//            String menuName = (String) ((JSONObject) menu).get("name");
//            if (menuName.equals(name)) {
//                return (JSONArray) ((JSONObject) menu).get("option");
//            }
//        }
//        return null;
//    }
}
