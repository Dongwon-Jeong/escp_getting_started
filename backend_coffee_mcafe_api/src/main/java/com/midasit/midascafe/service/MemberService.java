package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterMemberRq;
import org.springframework.http.HttpStatus;

import java.util.Map;

public interface MemberService {
    HttpStatus registerMember(RegisterMemberRq registerMemberRq);

    HttpStatus updateMember(String authorizationValue, Map<String,Object> updateMemberMap);

    HttpStatus deleteMember(String memberId);

    boolean verifyAuthorization(String authorizationValue);

    boolean verifyAuthorization(String authorizationValue, String memberId);

    boolean verifyPassword(String phone, String password);

    String getIdByPhone(String phone);
}
