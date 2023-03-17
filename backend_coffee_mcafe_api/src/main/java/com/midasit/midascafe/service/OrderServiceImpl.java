package com.midasit.midascafe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.midasit.midascafe.controller.rqrs.RegisterOrderRq;
import com.midasit.midascafe.dao.*;
import com.midasit.midascafe.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderDAO orderDAO;
    private final MemberDAO memberDAO;
    private final GroupDAO groupDAO;
    private final CommonService commonService;
    private final MenuService menuService;
    private final MemberService memberService;
    private final GroupService groupService;
    @Override
    public HttpStatus registerOrder(RegisterOrderRq registerOrderRq) {
        String groupId = registerOrderRq.getGroupId();
        if (!groupService.areExistIds(new ArrayList<>(Arrays.asList(groupId)))) {
            return HttpStatus.NOT_FOUND;
        }
        String phone = registerOrderRq.getPhone();
        String password = registerOrderRq.getPassword();
        String memberId = memberService.getIdByPhone(phone);
        if (!memberService.verifyPassword(phone, password)) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (memberId.isBlank()) {
            return HttpStatus.NOT_FOUND;
        }
        int quantity = registerOrderRq.getQuantity();
        String menuCode = registerOrderRq.getMenuCode();
        List<Integer> optionValueList = registerOrderRq.getOptionValueList();

        boolean setDefault = registerOrderRq.getSetDefault();
        if (setDefault) {
            // TODO: 기본 옵션 처리
        }
        Order order = Order.builder()
                .memberId(memberId)
                .menuCode(menuCode)
                .optionValueList(optionValueList)
                .quantity(quantity)
                .build();
        ResponseEntity<String> responseEntity = orderDAO.registerOrder(order);
        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            JsonNode responseJson;
            try {
                responseJson = commonService.getObjectMapper().readTree(responseEntity.getBody());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            String orderId = responseJson.get("items").get(0).get("_uuid").textValue();
            return groupService.addOrderId(groupId, orderId);
        }
        return responseEntity.getStatusCode();
    }

    @Override
    public Order getOrder(String orderId) {
        ResponseEntity<Order> orderResponseEntity = orderDAO.getOrder(orderId);
        if (orderResponseEntity.getStatusCode() == HttpStatus.OK) {
            return orderResponseEntity.getBody();
        }
        return null;
    }

    @Override
    public HttpStatus deleteOrder(String authorizationValue, String orderId) {
        Order order = getOrder(orderId);
        if (!memberService.verifyAuthorization(authorizationValue, order.getMemberId())) {
            return HttpStatus.UNAUTHORIZED;
        }

        List<Group> groupList = groupDAO.getGroupList();
        Optional<Group> groupOptional = groupList.stream()
                .filter(group -> group.getOrderIdList().contains(orderId))
                .findFirst();
        if (groupOptional.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }
        Group group = groupOptional.get();
        List<String> orderIdList = group.getOrderIdList();
        orderIdList.remove(orderId);
        groupService.removeOrderId(group.get_uuid(), orderIdList);
        return orderDAO.deleteOrder(orderId);
    }
}
