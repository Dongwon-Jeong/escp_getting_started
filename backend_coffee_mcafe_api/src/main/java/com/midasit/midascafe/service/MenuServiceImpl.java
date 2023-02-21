package com.midasit.midascafe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.midasit.midascafe.controller.rqrs.RegisterMenuRq;
import com.midasit.midascafe.dao.CommonDAO;
import com.midasit.midascafe.dao.MenuDAO;
import com.midasit.midascafe.dto.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService{
    private final MenuDAO menuDAO;
    private final CommonDAO commonDAO;
    private static WebClient menuClient;
    private Map<String, String> menuCodeToName = new HashMap<>();
    private Map<Long, Long> optionCodeToPrice = new HashMap<>();
    private int projectSeq = -1;

    @Override
    public int getProjectSeq() {
        if (projectSeq == -1) {
            upDateProjectSeq();
        }
        return projectSeq;
    }

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
        upDateProjectSeq();
        JsonNode responseJson = reqeustMenuData();
        return parseMenu(responseJson);
    }

    private void upDateProjectSeq() {
        if (menuClient == null) {
            menuClient = WebClient.builder()
                    .baseUrl("https://uchef.co.kr/")
                    .defaultHeader(HttpHeaders.ACCEPT, "*/*")
                    .build();
        }
        Mono<String> responseMono = menuClient.get()
                .uri("/webApp.action?shop_member_seq=1859&mode=5150")
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
        projectSeq = responseJson.get("searchResult").get("memberData").get("default_project_seq").asInt();
    }

    private JsonNode reqeustMenuData() {
        Mono<String> responseMono = menuClient.get()
                .uri("/webApp.action?mode=5151&shop_member_seq=1859&project_seq={projectSeq}", projectSeq)
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
        return responseJson;
    }

    private List<Menu> parseMenu(JsonNode responseJson) {
        List<Menu> menuList = new ArrayList<>();
        JsonNode menuPage = responseJson.get("searchResult").get("jsonData").get("PAGELIST").get("PAGE");
        for (int idx = 0; idx < menuPage.size(); idx++) {
            JsonNode ORDERBUTTONCOMP = menuPage.get(idx).get("LISTCOMP").get("LISTROW").get(0).get("ORDERBUTTONCOMP");
            int type = idx;
            ORDERBUTTONCOMP.forEach(menu -> menuList.add(Menu.builder()
                    .name(menu.get("menutitle").asText())
                    .code(menu.get("item_code").asText())
                    .unitPrice(menu.get("price").asInt())
                    .stock(menu.get("stock").asInt())
                    .type(type)
                    .build()));
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
    public String getMenuNameByMenuCode(String menuCode) {
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
    public Long getOptionPriceByOptionCode(Long optionCode, String menuCode) {
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
