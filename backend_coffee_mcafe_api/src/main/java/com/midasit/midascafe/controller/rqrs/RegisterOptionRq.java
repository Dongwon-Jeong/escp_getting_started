package com.midasit.midascafe.controller.rqrs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class RegisterOptionRq {
    @Schema(description = "옵션 이름 리스트")
    @NotNull
    List<String> name;

    @Schema(description = "옵션 코드 리스트")
    @NotNull
    List<Integer> code;

    @Schema(description = "가격 정보")
    List<Integer> price;

    @Schema(description = "필수 옵션 여부")
    Boolean essential;

    @Schema(description = "옵션 설명")
    String description;
}
