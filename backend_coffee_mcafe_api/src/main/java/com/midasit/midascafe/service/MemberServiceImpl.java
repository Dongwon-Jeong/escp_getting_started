package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterMemberRq;
import com.midasit.midascafe.dao.CellDAO;
import com.midasit.midascafe.dao.MemberDAO;
import com.midasit.midascafe.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor  // 생성자 주입
public class MemberServiceImpl implements MemberService{
    private final MemberDAO memberDAO;
    private final CellDAO cellDAO;
    @Override
    public int registerMember(RegisterMemberRq registerMemberRq) {
        String phone = registerMemberRq.getPhone();
        String name = registerMemberRq.getName();
        String cell = registerMemberRq.getCell();


        JSONArray items = memberDAO.getMemberList();
        for (Object item : items) {
            String mPhone = (String) ((JSONObject) item).get("phone");
            if(mPhone.equals(phone)) {
                return 409;
            }
        }

        String cellId = null;
        items = cellDAO.getCellList();
        for (Object item : items) {
            String cellName = (String) ((JSONObject) item).get("name");
            if (cellName.equals(cell)) {
                cellId = (String) ((JSONObject) item).get("_uuid");
            }
        }
        if(cellId == null) {
            return 404;
        }

        ResponseData postResponse = memberDAO.registerMember(phone, name, cellId);
        JSONParser parser = new JSONParser();
        JSONObject responseJson;
        try {
            responseJson = (JSONObject) parser.parse(postResponse.getResponseData());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String uuid = (String) ((JSONObject) ((JSONArray) responseJson.get("items")).get(0)).get("_uuid");
        cellDAO.addMember(cellId, uuid);

        return postResponse.getStatusCode();
    }
}
