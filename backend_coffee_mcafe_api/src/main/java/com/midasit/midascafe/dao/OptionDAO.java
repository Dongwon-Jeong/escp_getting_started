package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.PostResponse;
import org.json.simple.JSONObject;

import java.util.List;

public interface OptionDAO {
    PostResponse registerOption(List<String> name, List<Integer> code, List<Integer> price, boolean essential, String description);

    JSONObject getOptionById(String uuid);
}
