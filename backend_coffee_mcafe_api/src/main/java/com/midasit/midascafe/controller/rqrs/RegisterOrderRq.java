package com.midasit.midascafe.controller.rqrs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class RegisterOrderRq {
    @Schema(description = "주문한 그룹 아이디")
    @NotBlank
    private String groupId;
    @Schema(description = "주문자 휴대폰 번호")
    @NotBlank
    private String phone;
    @Schema(description = "M Cafe 직원확인 암호")
    @NotBlank
    private String password;
    @Schema(description = "주문 메뉴")
    @NotBlank
    private String menuCode;
    @Schema(description = "옵션 리스트")
    private List<Integer> optionValueList;
    @Schema(description = "수량")
    @NotNull
    private Integer quantity;
    @Schema(description = "고정 메뉴로 사용")
    @NotNull
    private Boolean setDefault;
}
