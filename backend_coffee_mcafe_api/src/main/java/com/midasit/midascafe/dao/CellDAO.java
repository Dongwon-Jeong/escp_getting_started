package com.midasit.midascafe.dao;

import org.json.simple.JSONArray;

import java.util.List;

public interface CellDAO {
    int registerCell(String name);
    List<String> findAll();
    JSONArray findMemberByCell(String cell);
    int modifyCellName(String uuid, String name);
    int addMember(String uuid, String member);
    int addOrder(String uuid, String order);
    int deleteOrder(String uuid, String order);
    int deleteOrder(String uuid);
    JSONArray getCellList();
    int deleteCell(String uuid);

    List<String> getOrderIdListByName(String cell);

    String getCellIdByName(String cell);
}
