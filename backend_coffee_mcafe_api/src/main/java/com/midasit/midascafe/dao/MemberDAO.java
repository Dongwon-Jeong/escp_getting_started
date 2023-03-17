package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.Member;
import com.midasit.midascafe.dto.ResponseData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public interface MemberDAO {
    HttpStatus registerMember(Member member);

    HttpStatus updateMember(String memberId, Map<String,Object> updateMemberMap);

    Map<String, String> getMemberMap();

    JSONObject getMemberById(String uuid);

    String getIdByPhone(String phone);

    String getGroupIdByPhone(String phone);

    String getNameByPhone(String phone);

    List<Member> getMemberList();

    HttpStatus deleteMember(String authorizationValue);

}
