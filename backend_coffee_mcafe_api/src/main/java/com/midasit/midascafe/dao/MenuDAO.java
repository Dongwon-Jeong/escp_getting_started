package com.midasit.midascafe.dao;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public interface MenuDAO {
    int registerMenu(String name, String code, int unitPrice, int type);
    JSONArray getMenuList();
}
