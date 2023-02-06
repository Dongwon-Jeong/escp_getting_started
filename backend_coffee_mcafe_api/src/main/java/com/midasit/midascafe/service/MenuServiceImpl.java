package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterMenuRq;
import com.midasit.midascafe.controller.rqrs.RegisterOptionGroupRq;
import com.midasit.midascafe.controller.rqrs.RegisterOptionValueRq;
import com.midasit.midascafe.dao.MenuDAO;
import com.midasit.midascafe.dao.OptionGroupDAO;
import com.midasit.midascafe.dao.OptionValueDAO;
import com.midasit.midascafe.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService{
    private final MenuDAO menuDAO;
    private final OptionGroupDAO optionGroupDAO;
    private final OptionValueDAO optionValueDAO;
    @Override
    public int registerMenu(RegisterMenuRq registerMenuRq) {
        String name = registerMenuRq.getName();
        String code = registerMenuRq.getCode();
        int unitPrice = registerMenuRq.getUnitPrice();
        List<String> optionGroupIdList = registerMenuRq.getOptionGroupIdList();
        for (String optionGroupId : optionGroupIdList) {
            JSONObject optionGroup = optionGroupDAO.getOptionGroupById(optionGroupId);
            if (optionGroup == null) {
                return 404;
            }
        }
        JSONArray items = menuDAO.getMenu();
        for (Object item : items) {
            String mName = (String) ((JSONObject) item).get("name");
            String mCode = (String) ((JSONObject) item).get("code");
            if (mName.equals(name) || mCode.equals(code)) {
                return 409;
            }
        }

        return menuDAO.registerMenu(name, code, unitPrice, optionGroupIdList);
    }

    @Override
    public int deleteMenu(int menu) {
        return 0;
    }


    @Override
    public ResponseData registerOptionGroup(RegisterOptionGroupRq registerOptionGroupRq) {
        String name = registerOptionGroupRq.getName();
        Boolean essential = registerOptionGroupRq.getEssential();
        String description = registerOptionGroupRq.getDescription();

        ResponseData responseData = optionGroupDAO.registerOptionGroup(name, essential, description);

        String uuid = extractUuid(responseData);
        responseData.setResponseData(uuid);
        return responseData;
    }

    @Override
    public ResponseData registerOptionValue(RegisterOptionValueRq registerOptionValueRq, String groupId) {
        int code = registerOptionValueRq.getCode();
        String name = registerOptionValueRq.getName();
        int price = registerOptionValueRq.getPrice();

        JSONObject optionGroup = optionGroupDAO.getOptionGroupById(groupId);
        if (optionGroup == null) {
            return ResponseData.builder()
                    .statusCode(404)
                    .build();
        }
        JSONArray optionValueIdList = (JSONArray) optionGroup.get("optionValueIdList");

        ResponseData responseData = optionValueDAO.registerOptionValue(code, name, price);
        String uuid = extractUuid(responseData);

        optionValueIdList.add(uuid);

        optionGroupDAO.addOptionValue(groupId, uuid);

        responseData.setResponseData(uuid);
        return responseData;
    }

    @Override
    public int deleteOptionValue(String groupId, String valueId) {
        if (optionGroupDAO.deleteOptionValue(groupId, valueId) == 404)
            return 404;
        return optionValueDAO.deleteOptionValue(valueId);
    }

    @Override
    public int deleteOptionGroup(String groupId) {
        JSONObject optionGroup = optionGroupDAO.getOptionGroupById(groupId);
        JSONArray optionValueIdList = (JSONArray) optionGroup.get("optionValueIdList");
        for(Object optionValueId : optionValueIdList) {
            optionValueDAO.deleteOptionValue((String) optionValueId);
        }
        return optionGroupDAO.deleteOptionGroup(groupId);
    }

    public String extractUuid(ResponseData responseData) {
        JSONParser parser = new JSONParser();
        JSONObject responseJson;
        try {
            responseJson = (JSONObject) parser.parse(responseData.getResponseData());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return (String) ((JSONObject) (((JSONArray) responseJson.get("items")).get(0))).get("_uuid");
    }
}
