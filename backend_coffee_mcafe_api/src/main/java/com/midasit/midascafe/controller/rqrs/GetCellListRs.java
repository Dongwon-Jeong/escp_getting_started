package com.midasit.midascafe.controller.rqrs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Builder
@Getter
public class GetCellListRs {
    @Schema(description = "셀 목록")
    @NotBlank
    private List<String> cellList;
}
