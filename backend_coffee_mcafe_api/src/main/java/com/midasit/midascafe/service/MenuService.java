package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterMenuRq;
import com.midasit.midascafe.dto.Menu;
import com.midasit.midascafe.dto.MenuDetail;

import java.util.List;

public interface MenuService {
    int registerMenu(RegisterMenuRq registerMenuRq);
    int deleteMenu(int menu);
    List<Menu> getMenuList();

    MenuDetail getMenuDetail(String menuCode);
}
