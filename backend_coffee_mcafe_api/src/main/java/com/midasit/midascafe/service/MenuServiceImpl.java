package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterMenuRq;
import com.midasit.midascafe.dao.CommonDAO;
import com.midasit.midascafe.dao.MenuDAO;
import com.midasit.midascafe.dto.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService{
    private final MenuDAO menuDAO;
    private final CommonDAO commonDAO;
    @Override
    public int registerMenu(RegisterMenuRq registerMenuRq) {
        String name = registerMenuRq.getName();
        String code = registerMenuRq.getCode();
        int unitPrice = registerMenuRq.getUnitPrice();
        int type = registerMenuRq.getType();

        JSONArray items = menuDAO.getMenuList();
        for (Object item : items) {
            String mName = (String) ((JSONObject) item).get("name");
            String mCode = (String) ((JSONObject) item).get("code");
            if (mName.equals(name) || mCode.equals(code)) {
                return 409;
            }
        }

        return menuDAO.registerMenu(name, code, unitPrice, type);
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
            menuList.add(Menu.builder()
                    .name((String) ((JSONObject) item).get("name"))
                    .code((String) ((JSONObject) item).get("code"))
                    .unitPrice((Long) ((JSONObject) item).get("unitPrice"))
                    .type((Long) ((JSONObject) item).get("type"))
                    .build());
        }
        return menuList;
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
                Boolean optionDefault = (Long) optionValueJson.get("option_default") == 1L;
                optionValueList.add(OptionValue.builder()
                        .name((String) optionValueJson.get("option_name"))
                        .code((Long) optionValueJson.get("option_seq"))
                        .price((Long) optionValueJson.get("option_price"))
                        .optionDefault(optionDefault)
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
}
