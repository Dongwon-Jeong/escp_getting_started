package com.midasit.midascafe.controller.rqrs;

import com.midasit.midascafe.dto.MenuDetail;
import com.midasit.midascafe.dto.OptionGroup;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MenuDetailRs {
    String name;
    String code;
    Long unitPrice;
    Long stock;
    List<OptionGroup> optionGroupList;

    public static MenuDetailRs of(MenuDetail menuDetail) {
        return MenuDetailRs.builder()
                .name(menuDetail.getName())
                .code(menuDetail.getCode())
                .unitPrice(menuDetail.getUnitPrice())
                .stock(menuDetail.getStock())
                .optionGroupList(menuDetail.getOptionGroupList())
                .build();
    }
}
