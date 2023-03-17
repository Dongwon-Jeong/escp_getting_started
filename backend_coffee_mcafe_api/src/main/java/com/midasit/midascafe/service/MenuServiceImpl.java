package com.midasit.midascafe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.midasit.midascafe.dao.CommonDAO;
import com.midasit.midascafe.dto.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService{
    private final CommonDAO commonDAO;
    private final CommonService commonService;
    private Map<String, String> menuCodeToName = new HashMap<>();
    private Map<Long, Long> optionCodeToPrice = new HashMap<>();

    @Override
    public List<Menu> getMenuList() {
        commonService.updateProjectSeq();
        JsonNode responseJson = reqeustMenuData();
        return parseMenu(responseJson);
    }

    private JsonNode reqeustMenuData() {
        Mono<String> responseMono = commonService.getUchefClient().get()
                .uri("/webApp.action?mode=5151&shop_member_seq=1859&project_seq={projectSeq}", commonService.getProjectSeq())
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
        Mono<String> responseMono = commonService.getUchefClient().get()
                .uri("/webApp.action?mode=5170&item_code={menuCode}&shop_member_seq=1859", menuCode)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);

        String responseStr = responseMono.block();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode menuJson;
        try {
            menuJson = objectMapper.readTree(responseStr);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        menuJson = menuJson.get("searchResult").get("list").get(0);
        String menuName = menuJson.get("item_name").textValue();
        Long unitPrice = menuJson.get("item_price").asLong();
        Long stock = menuJson.get("item_stock").asLong();
        List<OptionGroup> optionGroupList = new ArrayList<>();
        Map<Long, OptionValue> optionValueMap = new HashMap<>();
        menuJson = menuJson.get("option_group");
        for (JsonNode optionGroupJson : menuJson) {
            List<OptionValue> optionValueList = new ArrayList<>();
            JsonNode optionValueListNode = optionGroupJson.get("options");
            for (JsonNode optionValueJson : optionValueListNode) {
                OptionValue optionValue = OptionValue.builder()
                        .name(optionValueJson.get("option_name").textValue())
                        .code(optionValueJson.get("option_seq").asLong())
                        .price(optionValueJson.get("option_price").asLong())
                        .isOptionDefault(optionValueJson.get("option_default").asInt() == 1)
                        .build();
                optionValueList.add(optionValue);
                optionValueMap.put(optionValue.getCode(), optionValue);
            }
            optionGroupList.add(OptionGroup.builder()
                    .name(optionGroupJson.get("group_name").textValue())
                    .selectMin(optionGroupJson.get("group_min").asLong())
                    .selectMax(optionGroupJson.get("group_max").asLong())
                    .optionValueList(optionValueList)
                    .build());
        }
        String a = "123";

        return MenuDetail.builder()
                .name(menuName)
                .code(menuCode)
                .unitPrice(unitPrice)
                .stock(stock)
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
