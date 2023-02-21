package com.midasit.midascafe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class OptionGroup {
    private String name;
    private Long selectMin;
    private Long selectMax;
    private List<OptionValue> optionValueList;
}
