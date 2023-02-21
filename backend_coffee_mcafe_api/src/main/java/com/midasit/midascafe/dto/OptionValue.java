package com.midasit.midascafe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OptionValue {
    private String name;
    private Long code;
    private Long price;
    private Boolean isOptionDefault;
}
