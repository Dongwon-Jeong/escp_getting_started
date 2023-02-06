package com.midasit.midascafe.controller.rqrs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class RegisterOptionGroupRq {

    @Schema(description = "옵션 그룹 이름")
    @NotBlank
    String name;

    @Schema(description = "필수로 선택해야하는 옵션인지 여부")
    @NotNull
    Boolean essential;

    @Schema(description = "옵션 설명")
    String description;
}
