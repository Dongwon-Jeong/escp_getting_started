package com.midasit.midascafe.controller.rqrs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class RegisterOptionValueRq {
    @Schema(description = "옵션 값 이름")
    @NotBlank
    String name;
    @Schema(description = "옵션 값 코드")
    @NotNull
    Integer code;
    @Schema(description = "옵션 값 가격")
    @NotNull
    Integer price;
}
