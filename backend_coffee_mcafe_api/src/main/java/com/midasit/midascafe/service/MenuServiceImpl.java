package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterMenuRq;
import com.midasit.midascafe.controller.rqrs.RegisterOptionGroupRq;
import com.midasit.midascafe.controller.rqrs.RegisterOptionValueRq;
import com.midasit.midascafe.dao.CommonDAO;
import com.midasit.midascafe.dao.MenuDAO;
import com.midasit.midascafe.dao.OptionGroupDAO;
import com.midasit.midascafe.dao.OptionValueDAO;
import com.midasit.midascafe.dto.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService{
    private final MenuDAO menuDAO;
    private final OptionGroupDAO optionGroupDAO;
    private final OptionValueDAO optionValueDAO;
    private final CommonDAO commonDAO;
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
        JSONArray items = menuDAO.getMenuList();
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
    public List<Menu> getMenuList() {
        List<Menu> menuList = new ArrayList<>();
        JSONArray items = menuDAO.getMenuList();
        for (Object item : items) {
            String menuName = (String) ((JSONObject) item).get("name");
            String menuCode = (String) ((JSONObject) item).get("code");
            Long unitPrice = (Long) ((JSONObject) item).get("unitPrice");

            menuList.add(Menu.builder()
                    .name(menuName)
                    .code(menuCode)
                    .unitPrice(unitPrice)
                    .build());
        }
        return menuList;
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

    @Override
    public MenuDetail getMenuDetail(String menuCode) {
        String url = String.format("https://uchef.co.kr/webApp.action?mode=5170&item_code=%s&shop_member_seq=1859", menuCode);
        JSONObject menuItem = (JSONObject) ((JSONArray) ((JSONObject) (commonDAO.getItem(url).get("searchResult"))).get("list")).get(0);
        List<OptionGroup> optionGroupList = new ArrayList<>();

        JSONArray optionGroupJsonArray = (JSONArray) menuItem.get("option_group");
        for (Object optionGroupObj : optionGroupJsonArray) {
            JSONObject optionGroupJson = (JSONObject) optionGroupObj;
            List<OptionValue> optionValueList = new ArrayList<>();

            JSONArray optionValueJsonArray = (JSONArray) optionGroupJson.get("options");
            for (Object optionValueObj : optionValueJsonArray) {
                JSONObject optionValueJson = (JSONObject) optionValueObj;
                optionValueList.add(OptionValue.builder()
                        .name((String) optionValueJson.get("option_name"))
                        .code((Long) optionValueJson.get("option_seq"))
                        .price((Long) optionValueJson.get("option_price"))
                        .build());
            }

            optionGroupList.add(OptionGroup.builder()
                    .name((String) optionGroupJson.get("group_name"))
                    .selectMin((Long) optionGroupJson.get("group_min"))
                    .selectMax((Long) optionGroupJson.get("group_max"))
                    .optionValueList(optionValueList)
                    .build());
        }
        return MenuDetail.builder()
                .name((String) menuItem.get("item_name"))
                .code((String) menuItem.get("item_code"))
                .unitPrice((Long) menuItem.get("item_price"))
                .stock((Long) menuItem.get("item_stock"))
                .optionGroupList(optionGroupList)
                .build();
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
