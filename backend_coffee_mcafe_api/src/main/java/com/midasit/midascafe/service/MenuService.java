package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterMenuRq;
import com.midasit.midascafe.controller.rqrs.RegisterOptionRq;
import com.midasit.midascafe.dto.ResponseData;

public interface MenuService {
    int registerMenu(RegisterMenuRq registerMenuRq);
    int deleteMenu(int menu);
    ResponseData registerOption(RegisterOptionRq registerOptionRq);
}
