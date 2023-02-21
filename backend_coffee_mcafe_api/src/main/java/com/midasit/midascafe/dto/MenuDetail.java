package com.midasit.midascafe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
public class MenuDetail {
    private String name;
    private String code;
    private Long unitPrice;
    private Long stock;
    private List<OptionGroup> optionGroupList;
    private Map<Long, OptionValue> optionValueMap;
}
