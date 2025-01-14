package com.midasit.midascafe.controller.rqrs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class RegisterMemberRq {
    @Schema(description = "구성원의 휴대폰 번호")
    @NotBlank
    private String phone;
    @Schema(description = "구성원의 이름")
    @NotBlank
    private String name;
    @Schema(description = "M Cafe 직원확인 암호")
    @NotBlank
    private String password;
}
