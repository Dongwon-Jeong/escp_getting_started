package com.midasit.midascafe.controller;

import com.midasit.midascafe.controller.rqrs.MenuDetailRs;
import com.midasit.midascafe.controller.rqrs.RegisterMenuRq;
import com.midasit.midascafe.dto.Menu;
import com.midasit.midascafe.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
        } else if (statusCode == 404) {
            return new ResponseEntity<>("optionGroupId가 유효하지 않습니다.", HttpStatus.NOT_FOUND);
        } else if (statusCode == 409) {
            return new ResponseEntity<>("이미 등록된 메뉴입니다.", HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>("멤버 등록에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "메뉴 목록", description = "메뉴 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<Menu>> getMenuList() {
        List<Menu> menuList = menuService.getMenuList();
        return new ResponseEntity<>(menuList, HttpStatus.OK);
    }

    @Operation(summary = "메뉴 상세 정보", description = "메뉴 상세 정보를 조회합니다.")
    @GetMapping("/{menuCode}")
    public ResponseEntity<MenuDetailRs> getMenuDetail(@PathVariable("menuCode") String menuCode) {
        MenuDetailRs menuDetailRs = MenuDetailRs.of(menuService.getMenuDetail(menuCode));
        return new ResponseEntity<>(menuDetailRs, HttpStatus.OK);
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
}
