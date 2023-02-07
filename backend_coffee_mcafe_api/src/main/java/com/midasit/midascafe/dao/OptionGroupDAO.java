package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.ResponseData;
import org.json.simple.JSONObject;

public interface OptionGroupDAO {
    ResponseData registerOptionGroup(String name, boolean essential, String description);
    JSONObject getOptionGroupById(String optionGroupId);
    int deleteOptionGroup(String groupId);
    int addOptionValue(String groupId, String optionValueId);
    int deleteOptionValue(String groupId, String optionValueId);

}
