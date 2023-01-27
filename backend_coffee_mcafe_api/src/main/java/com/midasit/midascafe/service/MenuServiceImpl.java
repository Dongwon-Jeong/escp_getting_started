package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterMenuRq;
import com.midasit.midascafe.controller.rqrs.RegisterOptionRq;
import com.midasit.midascafe.dao.MenuDAO;
import com.midasit.midascafe.dao.OptionDAO;
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
    private final OptionDAO optionDAO;
    @Override
    public int registerMenu(RegisterMenuRq registerMenuRq) {
        String name = registerMenuRq.getName();
        String code = registerMenuRq.getCode();
        int unitPrice = registerMenuRq.getUnitPrice();
        List<String> option = registerMenuRq.getOption();
        JSONArray items = menuDAO.getMenu();
        for (Object item : items) {
            String mName = (String) ((JSONObject) item).get("name");
            String mCode = (String) ((JSONObject) item).get("code");
            if (mName.equals(name) || mCode.equals(code)) {
                return 409;
            }
        }

        return menuDAO.registerMenu(name, code, unitPrice, option);
    }

    @Override
    public int deleteMenu(int menu) {
        return 0;
    }


    @Override
    public ResponseData registerOption(RegisterOptionRq registerOptionRq) {
        List<String> nameList = registerOptionRq.getName();
        List<Integer> codeList = registerOptionRq.getCode();
        List<Integer> priceList = registerOptionRq.getPrice();
        Boolean essential = registerOptionRq.getEssential();
        String description = registerOptionRq.getDescription();


        ResponseData postResponse = optionDAO.registerOption(nameList, codeList, priceList, essential, description);

        JSONParser parser = new JSONParser();
        JSONObject responseJson;
        try {
            responseJson = (JSONObject) parser.parse(postResponse.getResponseData());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String uuid = (String) ((JSONObject) (((JSONArray) responseJson.get("items")).get(0))).get("_uuid");
        postResponse.setResponseData(uuid);
        return postResponse;
    }
}
