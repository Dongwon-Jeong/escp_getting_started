package com.midasit.midascafe.controller.rqrs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Builder
@Getter
public class GetMemberRs {
    @Schema(description = "이름")
    @NotBlank
    private String name;

    @Schema(description = "고정 메뉴")
    private String defaultMenu;

    @Schema(description = "고정 메뉴 옵션")
    private List<String> defaultOption;
}
