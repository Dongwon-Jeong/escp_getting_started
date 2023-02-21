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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService{
    private final MenuDAO menuDAO;
    private final CommonDAO commonDAO;
    private Map<String, String> menuCodeToName = new HashMap<>();
    private Map<Long, Long> optionCodeToPrice = new HashMap<>();
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
        Map<Long, OptionValue> optionValueMap = new HashMap<>();
        for (Object optionGroupObj : optionGroupJsonArray) {
            JSONObject optionGroupJson = (JSONObject) optionGroupObj;
            List<OptionValue> optionValueList = new ArrayList<>();

            JSONArray optionValueJsonArray = (JSONArray) optionGroupJson.get("options");
            for (Object optionValueObj : optionValueJsonArray) {
                JSONObject optionValueJson = (JSONObject) optionValueObj;
                Boolean isOptionDefault = ((Long) optionValueJson.get("option_default")) == 1L;
                OptionValue optionValue = OptionValue.builder()
                        .name((String) optionValueJson.get("option_name"))
                        .code((Long) optionValueJson.get("option_seq"))
                        .price((Long) optionValueJson.get("option_price"))
                        .isOptionDefault(isOptionDefault)
                        .build();
                optionValueList.add(optionValue);
                optionValueMap.put(optionValue.getCode(), optionValue);
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
                .optionValueMap(optionValueMap)
                .build();
    }

    @Override
    public String getMenuNameByMenuCode (String menuCode) {
        if (menuCodeToName.containsKey(menuCode)) {
            return menuCodeToName.get(menuCode);
        } else {
            MenuDetail menuDetail = getMenuDetail(menuCode);
            String menuName = menuDetail.getName();
            menuCodeToName.put(menuCode, menuName);
            List<OptionGroup> optionGroupList = menuDetail.getOptionGroupList();
            for (OptionGroup optionGroup : optionGroupList) {
                List<OptionValue> optionValueList = optionGroup.getOptionValueList();
                for (OptionValue optionValue : optionValueList) {
                    optionCodeToPrice.put(optionValue.getCode(), optionValue.getPrice());
                }
            }
            return menuName;
        }
    }

    @Override
    public Long getOptionPriceByOptionCode (Long optionCode, String menuCode) {
        if (!optionCodeToPrice.containsKey(optionCode)) {
            MenuDetail menuDetail = getMenuDetail(menuCode);
            String menuName = menuDetail.getName();
            menuCodeToName.put(menuCode, menuName);
            List<OptionGroup> optionGroupList = menuDetail.getOptionGroupList();
            for (OptionGroup optionGroup : optionGroupList) {
                List<OptionValue> optionValueList = optionGroup.getOptionValueList();
                for (OptionValue optionValue : optionValueList) {
                    optionCodeToPrice.put(optionValue.getCode(), optionValue.getPrice());
                }
            }
        }
        return optionCodeToPrice.get(optionCode);
    }
}
