package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterMenuRq;
import com.midasit.midascafe.dto.Menu;
import com.midasit.midascafe.dto.MenuDetail;
import lombok.Getter;

import java.util.List;

public interface MenuService {
    int getProjectSeq();
    int registerMenu(RegisterMenuRq registerMenuRq);
    int deleteMenu(int menu);
    List<Menu> getMenuList();
    MenuDetail getMenuDetail(String menuCode);

    String getMenuNameByMenuCode(String menuCode);

    Long getOptionPriceByOptionCode(Long optionCode, String menuCode);

}
