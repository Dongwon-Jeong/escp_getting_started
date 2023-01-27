package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.ModifyCellNameRq;
import com.midasit.midascafe.controller.rqrs.RegisterCellRq;
import com.midasit.midascafe.dao.CellDAO;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor  // 생성자 주입
public class CellServiceImpl implements CellService{
    private final CellDAO cellDAO;
    @Override
    public int registerCell(RegisterCellRq registerCellRq) {
        String name = registerCellRq.getName();
        List<String> cellList = cellDAO.findAll();
        if (cellList.contains(name)) {
            return 409;
        }

        return cellDAO.registerCell(name);
    }

    @Override
    public List<String> getCellList() {
        return cellDAO.findAll();
    }

    @Override
    public JSONArray getMemberList(String cell) {
        return cellDAO.findMemberByCell(cell);
    }

    @Override
    public int modifyCellName(String name, ModifyCellNameRq modifyCellNameRq) {
        JSONArray items = cellDAO.getCells();
        String uuid = null;
        boolean duplicate = false;
        for (Object item : items) {
            String cellName = (String) ((JSONObject) item).get("name");
            if (cellName.equals(modifyCellNameRq.getName())) {
                duplicate = true;
            }
            if (cellName.equals(name)) {
                uuid = (String) ((JSONObject) item).get("_uuid");
            }
        }
        if (uuid == null) { return 404; }
        if (duplicate) { return 409; }

        return cellDAO.modifyCellName(uuid, modifyCellNameRq.getName());
    }

    @Override
    public int deleteCell(String name) {
        JSONArray items = cellDAO.getCells();
        String uuid = null;
        boolean hasMember = false;
        for (Object item : items) {
            String cellName = (String) ((JSONObject) item).get("name");
            if (cellName.equals(name)) {
                uuid = (String) ((JSONObject) item).get("_uuid");
                if(((JSONObject) item).get("member") != null) {
                    hasMember = true;
                }
                break;
            }
        }
        if (uuid == null) { return 404; }
        if (hasMember) { return 403; }
        return cellDAO.deleteCell(uuid);
    }
}