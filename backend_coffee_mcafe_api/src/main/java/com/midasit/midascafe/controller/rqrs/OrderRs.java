package com.midasit.midascafe.controller.rqrs;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Builder
@Getter
public class OrderRs {
    private String name;
    private String menuName;
    private List<String> optionNameList;
    private int quantity;
}
