package com.midasit.midascafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Group {
    @Schema(description = "uuid")
    private String _uuid;
    @Schema(description = "그룹 이름")
    @NotBlank
    private String name;
    @Schema(description = "구성원의 휴대폰 번호")
    @NotBlank
    private List<String> orderIdList;
}
