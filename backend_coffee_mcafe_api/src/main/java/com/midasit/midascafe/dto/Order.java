package com.midasit.midascafe.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Order {
    String name;
    String menuName;
    List<String> optionNameList;
}