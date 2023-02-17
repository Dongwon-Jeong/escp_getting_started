package com.midasit.midascafe.dao;

import org.json.simple.JSONArray;

import java.util.List;

public interface CellDAO {
    int registerCell(String name);
    List<String> findAll();
    JSONArray findMemberByCell(String cell);
    int modifyCellName(String uuid, String name);
    int addMember(String uuid, String memberId);
    int deleteMember(String uuid, String memberId);
    JSONArray getCellList();
    int deleteCell(String uuid);

    String getCellIdByName(String cell);
}
