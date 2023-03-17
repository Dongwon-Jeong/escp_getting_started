package com.midasit.midascafe.controller;

import com.midasit.midascafe.controller.rqrs.RegisterMemberRq;
import com.midasit.midascafe.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Api(tags = "Member Controller")
@RestController
@RequestMapping("member")
@RequiredArgsConstructor  // 생성자 주입
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "멤버 등록", description = "새로운 멤버를 등록합니다.")
    @PostMapping
    public ResponseEntity<String> registerMember(@RequestBody @Valid RegisterMemberRq registerMemberRq) {
        HttpStatus statusCode = memberService.registerMember(registerMemberRq);

        if (statusCode == HttpStatus.CREATED) {
            return ResponseEntity.ok("멤버 등록 성공");
        } else if (statusCode == HttpStatus.CONFLICT) {
            return new ResponseEntity<>("이미 등록된 멤버입니다.", HttpStatus.CONFLICT);
        } else if (statusCode == HttpStatus.BAD_REQUEST) {
            return new ResponseEntity<>("휴대폰번호와 암호를 확인해주세요.", HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>("멤버 등록에 실패하였습니다.", statusCode);
        }
    }

    @Operation(summary = "멤버 업데이트", description = "멤버정보를 업데이트 합니다.")
    @PatchMapping
    public ResponseEntity<String> updateMember(@RequestHeader HttpHeaders headers,
                                               @RequestBody Map<String,Object> updateMemberRq) {
        String authorizationValue = headers.getFirst("Authorization");

        HttpStatus statusCode = memberService.updateMember(authorizationValue, updateMemberRq);

        if (statusCode == HttpStatus.OK) {
            return ResponseEntity.ok("멤버 수정 성공");
        } else if(statusCode == HttpStatus.NOT_FOUND) {
            return new ResponseEntity<>("멤버를 찾을 수 없습니다.", statusCode);
        } else {
            return new ResponseEntity<>("멤버 수정에 실패하였습니다.", statusCode);
        }
    }

    @Operation(summary = "멤버 삭제", description = "멤버를 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<String> deleteMember(@RequestHeader HttpHeaders headers) {
        String authorizationValue = headers.getFirst("Authorization");
        HttpStatus statusCode = memberService.deleteMember(authorizationValue);

        if (statusCode == HttpStatus.OK) {
            return ResponseEntity.ok("멤버 삭제 성공");
        } else {
            return new ResponseEntity<>("멤버 삭제에 실패하였습니다.", statusCode);
        }
    }
}
