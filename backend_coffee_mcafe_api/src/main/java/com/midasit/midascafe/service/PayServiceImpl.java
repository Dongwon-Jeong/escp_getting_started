package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.PayOrderRq;
import com.midasit.midascafe.dao.CellDAO;
import com.midasit.midascafe.dao.CommonDAO;
import com.midasit.midascafe.dao.MemberDAO;
import com.midasit.midascafe.dao.OrderDAO;
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
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

    private final MemberDAO memberDAO;
    private final CellDAO cellDAO;
    private final OrderDAO orderDAO;
    private final CommonDAO commonDAO;

    @Override
    public ResponseData payOrder(PayOrderRq payOrderRq) {
        String phone = payOrderRq.getPhone();
        String cell = payOrderRq.getCell();
        String name = memberDAO.getNameByPhone(phone);
        if (name == null) {
            return ResponseData.builder()
                    .statusCode(404)
                    .responseData("해당 사용자를 찾을 수 없습니다.")
                    .build();
        }
        String cellId = cellDAO.getCellIdByName(cell);
        List<String> cellOrderIdList = cellDAO.getOrderIdListByName(cell);
        if (cellOrderIdList == null || cellOrderIdList.size() == 0) {
            return ResponseData.builder()
                    .statusCode(404)
                    .responseData("해당 주문을 찾을 수 없습니다.")
                    .build();
        }
        long totalPrice = 0L;
        StringBuilder orderString = new StringBuilder("[");

        JSONArray orderList = new JSONArray();
        for (String orderId : cellOrderIdList) {
            JSONObject cellOrderObject = orderDAO.getOrder(orderId);
            String menuName = (String) cellOrderObject.get("menuName");
            String menuCode = (String) cellOrderObject.get("menuCode");
            JSONArray optionList = (JSONArray) cellOrderObject.get("options");
            boolean hasSameOrder = false;
            for (int idx = 0; idx < orderList.size(); idx++) {
                JSONObject orderObject = (JSONObject) orderList.get(idx);
                if (menuName.equals(orderObject.get("menuName")) && menuCode.equals(orderObject.get("menuCode")) &&
                        new HashSet<> (optionList).equals(new HashSet<> ((JSONArray)orderObject.get("options")))) {
                    orderObject.put("qty", (Integer) orderObject.get("qty") + 1);
                    hasSameOrder = true;
                    break;
                }
            }
            if(hasSameOrder) { continue; }
            cellOrderObject.put("qty", 1);
            orderList.add(cellOrderObject);
        }

        for (Object orderObject : orderList) {
            String menuName = (String) ((JSONObject) orderObject).get("menuName");
            String menuCode = (String) ((JSONObject) orderObject).get("menuCode");
            JSONArray optionList = (JSONArray) ((JSONObject) orderObject).get("options");
            JSONArray optionProcessed = new JSONArray();
            for (int idx = 0; idx < optionList.size(); idx++) {
                JSONArray option = new JSONArray();
                option.add(optionList.get(idx));
                option.add(1);
                option.add(1);
                option.add("");
                optionProcessed.add(option);
            }
            Long unitPrice = (Long) ((JSONObject) orderObject).get("price");
            Integer qty = (Integer) ((JSONObject) orderObject).get("qty");
            totalPrice += unitPrice;
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
        System.out.println(URL);
        HttpURLConnection connection = commonDAO.getConnection(URL.toString(), "GET");
        StringBuilder responseData = new StringBuilder();
        int statusCode = 200;
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
            cellDAO.deleteOrder(cellId);
            // loop를 사용하지 않고 한 번에 지우는 작업 해야함
            for (String cellOrderId : cellOrderIdList) {
                System.out.println(cellOrderId);
                orderDAO.deleteOrder(cellOrderId);
            }
        }

        return ResponseData.builder()
                .statusCode(statusCode)
                .responseData(responseData.toString())
                .build();
    }
}
