package com.midasit.midascafe.controller.rqrs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class PayOrderRq {
    @Schema(description = "결제자 휴대폰 번호")
    @NotBlank
    private String phone;

//    @Schema(description = "주문할 그룹의 이름")
//    @NotBlank
//    private String group;
}
