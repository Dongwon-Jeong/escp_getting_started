package com.midasit.midascafe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class Menu {
    private String name;
    private String code;
    private Integer unitPrice;
    private Integer stock;
    private Integer type;
}
