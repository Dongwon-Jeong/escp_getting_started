package com.midasit.midascafe.dao;

import org.json.simple.JSONArray;

public interface MenuDAO {
    int registerMenu(String name, String code, int unitPrice, int type);
    JSONArray getMenuList();
}
