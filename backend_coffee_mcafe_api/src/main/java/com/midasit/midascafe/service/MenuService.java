package com.midasit.midascafe.service;

import com.midasit.midascafe.dto.Menu;
import com.midasit.midascafe.dto.MenuDetail;

import java.util.List;

public interface MenuService {
    List<Menu> getMenuList();

    MenuDetail getMenuDetail(String menuCode);

    String getMenuNameByMenuCode(String menuCode);

    Long getOptionPriceByOptionCode(Long optionCode, String menuCode);

}
