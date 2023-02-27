package com.midasit.midascafe.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order {
    private String _uuid;
    private String memberId;
    private String menuCode;
    private List<Integer> optionValueList;
    private int quantity;
}