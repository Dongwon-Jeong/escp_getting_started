package com.midasit.midascafe.dto;

import com.midasit.midascafe.controller.rqrs.RegisterMemberRq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {
    @Schema(description = "uuid")
    private String _uuid;
    @Schema(description = "구성원의 휴대폰 번호")
    @NotBlank
    private String phone;
    @Schema(description = "구성원의 이름")
    @NotBlank
    private String name;
    @Schema(description = "구성원이 속한 그룹 Id")
    @NotNull
    private List<String> groupIdList;

    public static Member of(RegisterMemberRq registerMemberRq) {
        return Member.builder()
                .phone(registerMemberRq.getPhone())
                .name(registerMemberRq.getName())
                .groupIdList(new ArrayList<>())
                .build();
    }
}
