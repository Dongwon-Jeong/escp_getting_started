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
    Boolean essential;
    String description;
    List<OptionValue> optionValueList;
}
