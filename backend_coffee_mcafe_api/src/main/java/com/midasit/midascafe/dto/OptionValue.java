package com.midasit.midascafe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OptionValue {
    String name;
    Long code;
    Long price;
    Boolean optionDefault;
}
