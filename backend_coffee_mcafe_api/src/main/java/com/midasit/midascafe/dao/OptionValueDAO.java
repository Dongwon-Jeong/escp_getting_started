package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;

public interface OptionValueDAO {
    ResponseData registerOptionValue(int code, String name, int price);
    int deleteOptionValue(String  valueId);
}
