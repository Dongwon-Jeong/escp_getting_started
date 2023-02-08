package com.midasit.midascafe.controller.rqrs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
public class RegisterOrderRq {
    @Schema(description = "주문자 휴대폰 번호")
    @NotBlank
    String phone;
    @Schema(description = "주문 메뉴")
    @NotBlank
    String menuCode;
    @Schema(description = "옵션 리스트")
    List<Integer> optionValueList;
    @Schema(description = "고정 메뉴로 사용")
    Boolean setDefault;
}
