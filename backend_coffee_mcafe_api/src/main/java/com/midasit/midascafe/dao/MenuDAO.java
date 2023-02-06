package com.midasit.midascafe.dao;

import org.json.simple.JSONArray;

import java.util.List;

public interface MenuDAO {
    int registerMenu(String name, String code, int unitPrice, List<String> optionGroupIdList);
    JSONArray getMenuList();
}
