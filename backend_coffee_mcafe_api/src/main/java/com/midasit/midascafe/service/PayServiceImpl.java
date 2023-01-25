package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.PayOrderRq;
import com.midasit.midascafe.dao.CellDAO;
import com.midasit.midascafe.dao.MemberDAO;
import com.midasit.midascafe.dao.OrderDAO;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

    private final MemberDAO memberDAO;
    private final CellDAO cellDAO;
    private final OrderDAO orderDAO;

    @Override
    public int payOrder(PayOrderRq payOrderRq) {
        String phone = payOrderRq.getPhone();
        String cell = payOrderRq.getCell();
        String name = memberDAO.getNameByPhone(phone);
        List<String> orderIdList = cellDAO.getOrderIdListByName(cell);
        JSONArray orderArr = new JSONArray();
        long totalPrice = 0L;
        StringBuilder orderList = new StringBuilder("[");
        for (String orderId : orderIdList) {
            JSONObject orderObject = orderDAO.getOrder(orderId);
            String menuName = (String) orderObject.get("menuName");
            String menuCode = (String) orderObject.get("menuCode");
            JSONArray optionList = (JSONArray) orderObject.get("options");
            JSONArray optionProcessed = new JSONArray();
            for (int idx = 0; idx < optionList.size(); idx++) {
                JSONArray option = new JSONArray();
                option.add(optionList.get(idx));
                option.add(1);
                option.add(1);
                option.add("");
                optionProcessed.add(option);
            }
            Long unitPrice = (Long) orderObject.get("price");
            totalPrice += unitPrice;
            orderList.append("{\"menu_code\":\"");
            orderList.append(menuCode);
            orderList.append("\",\"menu_name\":\"");
            orderList.append(menuName);
            orderList.append("\",\"unit_price\":");
            orderList.append(unitPrice);
            orderList.append(",\"menu_qty\":\"1\",\"memo\":\"\",\"optionlist\":");
            orderList.append(optionProcessed);
            orderList.append("}");
        }
        orderList.append("]");

        StringBuilder URL = new StringBuilder("https://uchef.co.kr/webApp.action?mode=5060&table_name=TKO&order_list=")
                .append(orderList)
                .append("&order_cnt=0&cs_name=")
                .append(name)
                .append("&phone=")
                .append(phone)
                .append("&pickup_time=&address=&pay_type=&is_pre_pay=0&cs_email=&cs_id=&coupon_id=welfare%20discount&coupon_amount=")
                .append(totalPrice)
                .append("&point_amount=0&pay_tip=0&order_memo=&hoban_dc_json=&callback=angular.callbacks._5&shop_member_seq=1859&shop_id=&project_seq=7629");

        System.out.println(URL);
        return 0;
    }
}
