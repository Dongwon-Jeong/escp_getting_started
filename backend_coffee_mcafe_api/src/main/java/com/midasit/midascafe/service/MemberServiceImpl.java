package com.midasit.midascafe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.midasit.midascafe.controller.rqrs.RegisterMemberRq;
import com.midasit.midascafe.dao.CommonDAO;
import com.midasit.midascafe.dao.GroupDAO;
import com.midasit.midascafe.dao.MemberDAO;
import com.midasit.midascafe.dao.OrderDAO;
import com.midasit.midascafe.dto.Member;
import com.midasit.midascafe.dto.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor  // 생성자 주입
public class MemberServiceImpl implements MemberService{
    private final CommonDAO commonDAO;
    private final MemberDAO memberDAO;
    private final GroupDAO groupDAO;
    private final OrderDAO orderDAO;
    private final CommonService commonService;
    private final GroupService groupService;
    private final String URI = "/member";
    @Override
    public HttpStatus registerMember(RegisterMemberRq registerMemberRq) {
        String phone = registerMemberRq.getPhone();
        String password = registerMemberRq.getPassword();
        if (!verifyPassword(phone, password)) {
            return HttpStatus.BAD_REQUEST;
        }
        List<String> groupIdList = new ArrayList<>();
        Map<String, String> memberMap = memberDAO.getMemberMap();
        if (memberMap.containsKey(phone)) {
            return HttpStatus.CONFLICT;
        }
        if (!groupService.areExistIds(groupIdList)) {
            return HttpStatus.NOT_FOUND;
        }

        return memberDAO.registerMember(Member.of(registerMemberRq));
    }

    @Override
    public HttpStatus updateMember(String authorizationValue, Map<String,Object> updateMemberMap) {
        List<String> parsedAuthorization = commonService.parseAuthorizationValue(authorizationValue);
        String phone = parsedAuthorization.get(1);
        String password = parsedAuthorization.get(2);
        if (!parsedAuthorization.get(0).equals("Basic") ||
                !verifyPassword(phone, password)) {
            return HttpStatus.UNAUTHORIZED;
        }
        List<Member> memberList = memberDAO.getMemberList();
        Optional<Member> memberOptional = memberList.stream()
                .filter(x -> x.getPhone().equals(phone))
                .findFirst();
        if (memberOptional.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }
        String memberId = memberOptional.get().get_uuid();
        return memberDAO.updateMember(memberId, updateMemberMap);
    }

    @Override
    public boolean verifyAuthorization(String authorizationValue) {
        List<String> parsedAuthorization = commonService.parseAuthorizationValue(authorizationValue);
        String phone = parsedAuthorization.get(1);
        String password = parsedAuthorization.get(2);

        return parsedAuthorization.get(0).equals("Basic") && verifyPassword(phone, password);
    }

    @Override
    public boolean verifyAuthorization(String authorizationValue, String memberId) {
        List<String> parsedAuthorization = commonService.parseAuthorizationValue(authorizationValue);
        String phone = parsedAuthorization.get(1);
        String password = parsedAuthorization.get(2);

        return parsedAuthorization.get(0).equals("Basic") && verifyPassword(phone, password) && memberId.equals(getIdByPhone(phone));
    }

    @Override
    public boolean verifyPassword(String phone, String password) {
        WebClient uchefClient = commonService.getUchefClient();
        Mono<String> responseMono = uchefClient.get()
                .uri("webApp.action?mode=7100&shop_member_seq=1859&coupon_type=3&phone_id={phone}&passwd={password}&project_seq={projectSeq}", phone, password, commonService.getProjectSeq())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
        String responseStr = responseMono.block();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson;
        try {
            responseJson = objectMapper.readTree(responseStr);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return responseJson.get("resultCode").asInt() == 0;
    }

    @Override
    public HttpStatus deleteMember(String authorizationValue) {
        List<String> parsedAuthorization = commonService.parseAuthorizationValue(authorizationValue);
        String phone = parsedAuthorization.get(1);
        String password = parsedAuthorization.get(2);
        if (!parsedAuthorization.get(0).equals("Basic") ||
                !verifyPassword(phone, password)) {
            return HttpStatus.UNAUTHORIZED;
        }
        List<Member> memberList = memberDAO.getMemberList();
        Optional<Member> memberOptional = memberList.stream()
                .filter(x -> x.getPhone().equals(phone))
                .findFirst();
        if (memberOptional.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }
        String memberId = memberOptional.get().get_uuid();
        // order 삭제
        List<Order> orderList = orderDAO.getOrderList();
        List<String> orderIdListForDelete = new ArrayList<>();
        orderList.stream()
                .filter(order -> order.getMemberId().equals(memberId))
                .forEach(order -> orderIdListForDelete.add(order.get_uuid()));
        List<Map<String, Object>> body = new ArrayList<>();
        orderIdListForDelete.forEach(orderId -> body.add(Map.of("_uuid", orderId)));
        if (body.size() != 0) {
            orderDAO.deleteOrders(commonService.getObjectMapper().valueToTree(body));
        }
        groupService.removeOrderId(orderIdListForDelete);
        return memberDAO.deleteMember(memberId);
    }

    @Override
    public String getIdByPhone(String phone) {
        List<Member> memberList = memberDAO.getMemberList();
        Optional<Member> memberOptional = memberList.stream()
                .filter(x -> x.getPhone().equals(phone))
                .findFirst();
        return memberOptional.isPresent() ? memberOptional.get().get_uuid() : "";
    }
}
