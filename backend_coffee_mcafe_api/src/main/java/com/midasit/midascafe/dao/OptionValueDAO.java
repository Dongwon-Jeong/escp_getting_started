package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;
import org.json.simple.JSONObject;

public interface OptionValueDAO {
    ResponseData registerOptionValue(int code, String name, int price);
    JSONObject getOptionValueById(String optionValueId);
    int deleteOptionValue(String  valueId);
}
