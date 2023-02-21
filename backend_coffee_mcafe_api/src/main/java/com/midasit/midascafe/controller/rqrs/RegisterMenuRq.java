package com.midasit.midascafe.controller.rqrs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class RegisterMenuRq {
    @Schema(description = "메뉴 이름")
    @NotBlank
    private String name;

    @Schema(description = "메뉴 코드")
    @NotBlank
    private String code;

    @Schema(description = "가격")
    @NotNull
    private Integer unitPrice;

    @Schema(description = "0: COFFEE, 1: NON-COFFEE, 2: ADE & JUICE, 3: FLATCCINO, 4: BOTTLE, 5: SNACK, 6: S/W")
    private Integer type;
}
