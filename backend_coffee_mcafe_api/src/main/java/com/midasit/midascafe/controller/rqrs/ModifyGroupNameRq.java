package com.midasit.midascafe.controller.rqrs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ModifyGroupNameRq {
    @Schema(description = "변경할 그룹의 기존 이름")
    @NotBlank
    private String name;
}
