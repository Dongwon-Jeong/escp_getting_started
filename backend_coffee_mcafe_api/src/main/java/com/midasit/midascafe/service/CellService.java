package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.ModifyCellNameRq;
import com.midasit.midascafe.controller.rqrs.RegisterCellRq;
import org.json.simple.JSONArray;

import java.util.List;

public interface CellService {

    int registerCell(RegisterCellRq registerCellRq);
    List<String> getCellList();

    JSONArray getMemberList(String cell);
    int modifyCellName(String name, ModifyCellNameRq modifyCellNameRq);

    int deleteCell(String name);
}
