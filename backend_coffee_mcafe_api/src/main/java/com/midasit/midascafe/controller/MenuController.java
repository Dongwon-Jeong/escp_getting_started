package com.midasit.midascafe.controller;

import com.midasit.midascafe.controller.rqrs.RegisterMenuRq;
import com.midasit.midascafe.controller.rqrs.RegisterOptionGroupRq;
import com.midasit.midascafe.controller.rqrs.RegisterOptionValueRq;
import com.midasit.midascafe.dto.Menu;
import com.midasit.midascafe.dto.ResponseData;
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
    public ResponseEntity getMenuList() {
        List<Menu> menuList = menuService.getMenuList();
        return new ResponseEntity<>(menuList, HttpStatus.OK);
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

    @Operation(summary = "옵션 그룹 등록", description = "새로운 옵션을 등록합니다.")
    @PostMapping("/option/group")
    public ResponseEntity<String> registerOptionGroup(@RequestBody @Valid RegisterOptionGroupRq registerOptionGroupRq) {
        ResponseData postResponse = menuService.registerOptionGroup(registerOptionGroupRq);
        int statusCode = postResponse.getStatusCode();

        if (statusCode == 201) {
            return ResponseEntity.ok(postResponse.getResponseData());
        } else {
            return new ResponseEntity<>("옵션 등록에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "옵션 그룹 삭제", description = "옵션 그룹을 삭제합니다.")
    @DeleteMapping("/option/{groupId}")
    public ResponseEntity<String> deleteOptionGroup(@PathVariable("groupId") String groupId) {
        int statusCode = menuService.deleteOptionGroup(groupId);

        if (statusCode == 200) {
            return ResponseEntity.ok("옵션 그룹 삭제 성공");
        } else if (statusCode == 404) {
            return new ResponseEntity<>("groupId가 유효하지 않습니다.", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("옵션 값 삭제에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "옵션 값 등록", description = "새로운 옵션 값을 등록합니다.")
    @PostMapping("/option/{groupId}/value")
    public ResponseEntity<String> registerOptionValue(@RequestBody @Valid RegisterOptionValueRq registerOptionValueRq, @PathVariable("groupId") String groupId) {
        ResponseData postResponse = menuService.registerOptionValue(registerOptionValueRq, groupId);
        int statusCode = postResponse.getStatusCode();
        if (statusCode == 201) {
            return ResponseEntity.ok(postResponse.getResponseData());
        } else if (statusCode == 404) {
            return new ResponseEntity<>("groupId가 유효하지 않습니다.", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("옵션 값 등록에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "옵션 값 삭제", description = "옵션 값을 삭제합니다.")
    @DeleteMapping("/option/{groupId}/{valueId}")
    public ResponseEntity<String> deleteOptionValue(@PathVariable("groupId") String groupId, @PathVariable("valueId") String valueId) {
        int statusCode = menuService.deleteOptionValue(groupId, valueId);
        if (statusCode == 200) {
            return ResponseEntity.ok("옶견 값 삭제 성공");
        } else if (statusCode == 400) {
            return new ResponseEntity<>("valueId가 유효하지 않습니다.", HttpStatus.NOT_FOUND);
        } else if (statusCode == 404) {
            return new ResponseEntity<>("groupId가 유효하지 않습니다.", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("옵션 값 삭제에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }
}
