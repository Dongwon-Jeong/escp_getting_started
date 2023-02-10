package com.midasit.midascafe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class MenuDetail {
    String name;
    String code;
    Long unitPrice;
    Long stock;
    List<OptionGroup> optionGroupList;
}
