package com.midasit.midascafe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class OptionGroup {
    String name;
    Long selectMin;
    Long selectMax;
    List<OptionValue> optionValueList;
}
