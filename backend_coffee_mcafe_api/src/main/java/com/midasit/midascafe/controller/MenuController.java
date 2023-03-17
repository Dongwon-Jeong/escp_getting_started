package com.midasit.midascafe.controller;

import com.midasit.midascafe.controller.rqrs.MenuDetailRs;
import com.midasit.midascafe.dto.Menu;
import com.midasit.midascafe.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Menu Controller")
@RestController
@RequestMapping("menu")
@RequiredArgsConstructor  // 생성자 주입
public class MenuController {
    private final MenuService menuService;

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

}
