package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterMenuRq;
import com.midasit.midascafe.controller.rqrs.RegisterOptionGroupRq;
import com.midasit.midascafe.controller.rqrs.RegisterOptionValueRq;
import com.midasit.midascafe.dto.ResponseData;

public interface MenuService {
    int registerMenu(RegisterMenuRq registerMenuRq);
    int deleteMenu(int menu);
    ResponseData registerOptionGroup(RegisterOptionGroupRq registerOptionRq);

    ResponseData registerOptionValue(RegisterOptionValueRq registerOptionValueRq, String groupId);

    int deleteOptionValue(String groupId, String valueId);

    int deleteOptionGroup(String groupId);
}
