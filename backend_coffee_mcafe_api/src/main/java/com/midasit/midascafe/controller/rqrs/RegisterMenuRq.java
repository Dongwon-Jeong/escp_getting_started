package com.midasit.midascafe.controller.rqrs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
public class RegisterMenuRq {
    @Schema(description = "메뉴 이름")
    @NotBlank
    String name;

    @Schema(description = "메뉴 코드")
    @NotBlank
    String code;

    @Schema(description = "가격")
    int unitPrice;

    @Schema(description = "옵션 uuid")
    List<String> option;
}
