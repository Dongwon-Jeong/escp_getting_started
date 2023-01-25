package com.midasit.midascafe.controller.rqrs;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;


@Getter
public class RegisterCellRq {
    @Schema(description = "등록할 셀의 이름")
    @NotBlank
    private String name;
}
