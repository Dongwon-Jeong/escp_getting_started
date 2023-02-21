package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.PayOrderRq;
import com.midasit.midascafe.dao.CellDAO;
import com.midasit.midascafe.dao.CommonDAO;
import com.midasit.midascafe.dao.MemberDAO;
import com.midasit.midascafe.dao.OrderDAO;
import com.midasit.midascafe.dto.MenuDetail;
import com.midasit.midascafe.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

    private final MemberDAO memberDAO;
    private final OrderDAO orderDAO;
    private final CommonDAO commonDAO;
    private final MenuService menuService;

    @Override
    public ResponseData payOrder(PayOrderRq payOrderRq) {
        String phone = payOrderRq.getPhone();
        //String cellName = payOrderRq.getCell();
        String name = memberDAO.getNameByPhone(phone);
        if (name == null) {
            return ResponseData.builder()
                    .statusCode(404)
                    .responseData("해당 사용자를 찾을 수 없습니다.")
                    .build();
        }
        String cellId = memberDAO.getCellIdByPhone(phone);
        //String cellId = cellDAO.getCellIdByName(cellName);

        JSONArray orderList = orderDAO.getOrderList();
        List<String> cellOrderIdList = new ArrayList<>();
        for (Object orderObj : orderList) {
            JSONObject orderJsonObj = (JSONObject) orderObj;
            if (orderJsonObj.get("cellId").equals(cellId)) {
                cellOrderIdList.add((String) orderJsonObj.get("_uuid"));
            }
        }
        if (cellOrderIdList.size() == 0) {
            return ResponseData.builder()
                    .statusCode(404)
                    .responseData("해당 주문을 찾을 수 없습니다.")
                    .build();
        }
        long totalPrice = 0L;
        StringBuilder orderString = new StringBuilder("[");

        JSONArray payOrderList = new JSONArray();
        for (String orderId : cellOrderIdList) {
            JSONObject cellOrderObject = orderDAO.getOrder(orderId);
            String menuCode = (String) cellOrderObject.get("menuCode");
            JSONArray optionList = (JSONArray) cellOrderObject.get("optionValueList");
            boolean hasSameOrder = false;
            for (int idx = 0; idx < payOrderList.size(); idx++) {
                JSONObject payOrderObject = (JSONObject) payOrderList.get(idx);
                if (menuCode.equals(payOrderObject.get("menuCode")) &&
                        new HashSet<> (optionList).equals(new HashSet<> ((JSONArray)payOrderObject.get("optionValueList")))) {
                    payOrderObject.put("qty", (Integer) payOrderObject.get("qty") + 1);
                    hasSameOrder = true;
                    break;
                }
            }
            if(hasSameOrder) { continue; }
            cellOrderObject.put("qty", 1);
            payOrderList.add(cellOrderObject);
        }

        for (Object orderObject : payOrderList) {
            String menuCode = (String) ((JSONObject) orderObject).get("menuCode");
            String menuName;
            menuName = menuService.getMenuNameByMenuCode(menuCode);
            MenuDetail menuDetail = menuService.getMenuDetail(menuCode);

            JSONArray optionList = (JSONArray) ((JSONObject) orderObject).get("optionValueList");
            JSONArray optionProcessed = new JSONArray();
            Long price = 0L;
            for (int idx = 0; idx < optionList.size(); idx++) {
                JSONArray option = new JSONArray();
                Long optionCode = (Long) optionList.get(idx);
                Long optionPrice = menuService.getOptionPriceByOptionCode(optionCode, menuCode);
                price += optionPrice;
                option.add(optionCode);
                option.add(1);
                option.add(1);
                option.add("");
                optionProcessed.add(option);
            }
            Long unitPrice = menuDetail.getUnitPrice();
            price += unitPrice;
            Integer qty = (Integer) ((JSONObject) orderObject).get("qty");
            orderString.append("{\"menu_code\":\"");
            orderString.append(menuCode);
            orderString.append("\",\"menu_name\":\"");
            orderString.append(menuName);
            orderString.append("\",\"unit_price\":");
            orderString.append(unitPrice);
            orderString.append(",\"menu_qty\":\"");
            orderString.append(qty);
            orderString.append("\",\"memo\":\"\",\"optionlist\":");
            orderString.append(optionProcessed);
            orderString.append("},");
            totalPrice += price * qty;
        }
        orderString.deleteCharAt(orderString.length() - 1);
        orderString.append("]");

        StringBuilder URL = null;
        try {
            URL = new StringBuilder("https://uchef.co.kr/webApp.action?mode=5060&table_name=TKO&order_list=")
                    .append(URLEncoder.encode(orderString.toString(), "UTF-8"))
                    .append("&order_cnt=0&cs_name=")
                    .append(URLEncoder.encode(name, "UTF-8"))
                    .append("&phone=")
                    .append(phone)
                    .append("&pickup_time=&address=&pay_type=&is_pre_pay=0&cs_email=&cs_id=&coupon_id=welfare%20discount&coupon_amount=")
                    .append(totalPrice)
                    .append("&point_amount=0&pay_tip=0&order_memo=&hoban_dc_json=&callback=angular.callbacks._5&shop_member_seq=1859&shop_id=&project_seq=")
                    .append(menuService.getProjectSeq());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(URL);
        HttpURLConnection connection = commonDAO.getConnection(URL.toString(), "GET");
        StringBuilder responseData = new StringBuilder();
        int statusCode;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                responseData.append(inputLine);
            }
            statusCode = connection.getResponseCode();
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (statusCode == 200) {
            JSONArray cellOrderIdJsonArray = new JSONArray();
            for (String cellOrderId : cellOrderIdList) {
                JSONObject cellOrderIdJsonObj = new JSONObject();
                cellOrderIdJsonObj.put("_uuid", cellOrderId);
                cellOrderIdJsonArray.add(cellOrderIdJsonObj);
            }
            orderDAO.deleteOrder(cellOrderIdJsonArray);
        }
        return ResponseData.builder()
                .statusCode(statusCode)
                .responseData(responseData.toString())
                .build();
    }

    @Override
    public ResponseData fakePay(PayOrderRq payOrderRq) {
        String phone = payOrderRq.getPhone();
        //String cellName = payOrderRq.getCell();
        String name = memberDAO.getNameByPhone(phone);
        if (name == null) {
            return ResponseData.builder()
                    .statusCode(404)
                    .responseData("해당 사용자를 찾을 수 없습니다.")
                    .build();
        }
        String cellId = memberDAO.getCellIdByPhone(phone);
        //String cellId = cellDAO.getCellIdByName(cellName);

        JSONArray orderList = orderDAO.getOrderList();
        List<String> cellOrderIdList = new ArrayList<>();
        for (Object orderObj : orderList) {
            JSONObject orderJsonObj = (JSONObject) orderObj;
            if (orderJsonObj.get("cellId").equals(cellId)) {
                cellOrderIdList.add((String) orderJsonObj.get("_uuid"));
            }
        }
        if (cellOrderIdList.size() == 0) {
            return ResponseData.builder()
                    .statusCode(404)
                    .responseData("해당 주문을 찾을 수 없습니다.")
                    .build();
        }
        long totalPrice = 0L;
        StringBuilder orderString = new StringBuilder("[");

        JSONArray payOrderList = new JSONArray();
        for (String orderId : cellOrderIdList) {
            JSONObject cellOrderObject = orderDAO.getOrder(orderId);
            String menuCode = (String) cellOrderObject.get("menuCode");
            JSONArray optionList = (JSONArray) cellOrderObject.get("optionValueList");
            boolean hasSameOrder = false;
            for (int idx = 0; idx < payOrderList.size(); idx++) {
                JSONObject payOrderObject = (JSONObject) payOrderList.get(idx);
                if (menuCode.equals(payOrderObject.get("menuCode")) &&
                        new HashSet<> (optionList).equals(new HashSet<> ((JSONArray)payOrderObject.get("optionValueList")))) {
                    payOrderObject.put("qty", (Integer) payOrderObject.get("qty") + 1);
                    hasSameOrder = true;
                    break;
                }
            }
            if(hasSameOrder) { continue; }
            cellOrderObject.put("qty", 1);
            payOrderList.add(cellOrderObject);
        }

        for (Object orderObject : payOrderList) {
            String menuCode = (String) ((JSONObject) orderObject).get("menuCode");
            String menuName;
            menuName = menuService.getMenuNameByMenuCode(menuCode);
            MenuDetail menuDetail = menuService.getMenuDetail(menuCode);

            JSONArray optionList = (JSONArray) ((JSONObject) orderObject).get("optionValueList");
            JSONArray optionProcessed = new JSONArray();
            Long price = 0L;
            for (int idx = 0; idx < optionList.size(); idx++) {
                JSONArray option = new JSONArray();
                Long optionCode = (Long) optionList.get(idx);
                Long optionPrice = menuService.getOptionPriceByOptionCode(optionCode, menuCode);
                price += optionPrice;
                option.add(optionCode);
                option.add(1);
                option.add(1);
                option.add("");
                optionProcessed.add(option);
            }
            Long unitPrice = menuDetail.getUnitPrice();
            price += unitPrice;
            Integer qty = (Integer) ((JSONObject) orderObject).get("qty");
            orderString.append("{\"menu_code\":\"");
            orderString.append(menuCode);
            orderString.append("\",\"menu_name\":\"");
            orderString.append(menuName);
            orderString.append("\",\"unit_price\":");
            orderString.append(unitPrice);
            orderString.append(",\"menu_qty\":\"");
            orderString.append(qty);
            orderString.append("\",\"memo\":\"\",\"optionlist\":");
            orderString.append(optionProcessed);
            orderString.append("},");
            totalPrice += price * qty;
        }
        orderString.deleteCharAt(orderString.length() - 1);
        orderString.append("]");

        StringBuilder URL = null;
        try {
            URL = new StringBuilder("https://uchef.co.kr/webApp.action?mode=5060&table_name=TKO&order_list=")
                    .append(URLEncoder.encode(orderString.toString(), "UTF-8"))
                    .append("&order_cnt=0&cs_name=")
                    .append(URLEncoder.encode(name, "UTF-8"))
                    .append("&phone=")
                    .append(phone)
                    .append("&pickup_time=&address=&pay_type=&is_pre_pay=0&cs_email=&cs_id=&coupon_id=welfare%20discount&coupon_amount=")
                    .append(totalPrice)
                    .append("&point_amount=0&pay_tip=0&order_memo=&hoban_dc_json=&callback=angular.callbacks._5&shop_member_seq=1859&shop_id=&project_seq=7629");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
//        System.out.println(URL);
        int statusCode = 200;
//        HttpURLConnection connection = commonDAO.getConnection(URL.toString(), "GET");
//        StringBuilder responseData = new StringBuilder();
//        int statusCode;
//        try {
//            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String inputLine;
//            while ((inputLine = br.readLine()) != null) {
//                responseData.append(inputLine);
//            }
//            statusCode = connection.getResponseCode();
//            connection.disconnect();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        if (statusCode == 200) {
            JSONArray cellOrderIdJsonArray = new JSONArray();
            for (String cellOrderId : cellOrderIdList) {
                JSONObject cellOrderIdJsonObj = new JSONObject();
                cellOrderIdJsonObj.put("_uuid", cellOrderId);
                cellOrderIdJsonArray.add(cellOrderIdJsonObj);
            }
            orderDAO.deleteOrder(cellOrderIdJsonArray);
        }
        return ResponseData.builder()
                .statusCode(statusCode)
                .responseData("2302170486")
                .build();
    }
}
