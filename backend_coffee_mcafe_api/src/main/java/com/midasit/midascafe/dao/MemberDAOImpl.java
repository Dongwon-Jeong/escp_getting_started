package com.midasit.midascafe.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.midasit.midascafe.dto.Member;
import com.midasit.midascafe.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor  // 생성자 주입
public class MemberDAOImpl implements MemberDAO{

    private final CommonDAO commonDAO;
    private final CommonService commonService;
    private final static String URL = "https://crudapi.co.uk/api/v1/member";
    private final static String URI = "/member";


    @Override
    public HttpStatus registerMember(Member member) {
        List<JsonNode> body = new ArrayList<>();
        ObjectNode data = commonService.getObjectMapper()
                .valueToTree(member);
        data.remove("_uuid");
        body.add(data);
        ObjectMapper objectMapper = commonService.getObjectMapper();
        ResponseEntity<String> responseEntity = commonDAO.postRequest(URI, objectMapper.valueToTree(body), String.class).block();

        return responseEntity.getStatusCode();
    }

    @Override
    public HttpStatus updateMember(String memberId, Map<String,Object> updateMemberMap) {
        ObjectNode data = commonService.getObjectMapper()
                .valueToTree(updateMemberMap);
        ObjectMapper objectMapper = commonService.getObjectMapper();
        ResponseEntity<String> responseEntity = commonDAO.putRequest(URI + "/" + memberId, objectMapper.valueToTree(data), String.class).block();

        return responseEntity.getStatusCode();
    }

    @Override
    public Map<String, String> getMemberMap() {
        return commonDAO.getItemMap(URI, "phone", "name");
    }

    @Override
    public JSONObject getMemberById(String uuid) {
        JSONObject member;
        member = commonDAO.getItem(URL + "/" + uuid);
        return member;
    }

    @Override
    public String getIdByPhone(String phone) {

//        return getDataByPhone(phone, "_uuid");
        return null;
    }

    @Override
    public String getGroupIdByPhone(String phone) {
        return null;
    }

    @Override
    public String getNameByPhone(String phone) {
        return null;
    }
    @Override
    public List<Member> getMemberList() {
        return commonDAO.getItemList(URI, Member.class);
    }

    @Override
    public HttpStatus deleteMember(String memberId) {
        String uri = new StringBuilder(URI).append("/").append(memberId).toString();
        ResponseEntity<String> responseEntity = commonDAO.deleteRequest(uri, String.class).block();
        return responseEntity.getStatusCode();
    }
}
