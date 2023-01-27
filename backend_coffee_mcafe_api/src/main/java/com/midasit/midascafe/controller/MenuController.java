package com.midasit.midascafe.controller;

import com.midasit.midascafe.controller.rqrs.RegisterMenuRq;
import com.midasit.midascafe.controller.rqrs.RegisterOptionRq;
import com.midasit.midascafe.dto.ResponseData;
import com.midasit.midascafe.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Menu Controller")
@RestController
@RequestMapping("menu")
@RequiredArgsConstructor  // 생성자 주입
public class MenuController {
    private final MenuService menuService;
    @Operation(summary = "메뉴 등록", description = "새로운 메뉴를 등록합니다.")
    @PostMapping
    public ResponseEntity<String> registerMenu(@RequestBody @Valid RegisterMenuRq registerMenuRq) {
        int statusCode = menuService.registerMenu(registerMenuRq);

        if (statusCode == 201) {
            return ResponseEntity.ok("메뉴 등록 성공");
        } else if (statusCode == 409) {
            return new ResponseEntity<>("이미 등록된 메뉴입니다.", HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>("멤버 등록에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다.")
    @DeleteMapping("/{code}")
    public ResponseEntity<String> deleteMenu(@PathVariable(value = "code") int menu) {
        int statusCode = 0;

        if (statusCode == 200) {
            return ResponseEntity.ok("메뉴 삭제 성공");
        } else if (statusCode == 404) {
            return new ResponseEntity<>("해당 메뉴가 없습니다.", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("메뉴 삭제에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "옵션 등록", description = "새로운 옵션을 등록합니다.")
    @PostMapping("/option")
    public ResponseEntity<String> registerOption(@RequestBody @Valid RegisterOptionRq registerOptionRq) {
        ResponseData postResponse = menuService.registerOption(registerOptionRq);
        int statusCode = postResponse.getStatusCode();

        if (statusCode == 201) {
            return ResponseEntity.ok(postResponse.getResponseData());
        } else {
            return new ResponseEntity<>("옵션 등록에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }
}
