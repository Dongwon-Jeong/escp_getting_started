package com.midasit.midascafe.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Order {
    private String name;
    private String menuName;
    private List<String> optionNameList;
}