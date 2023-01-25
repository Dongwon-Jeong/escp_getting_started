package com.midasit.midascafe.controller;


import com.midasit.midascafe.controller.rqrs.ModifyCellNameRq;
import com.midasit.midascafe.controller.rqrs.RegisterCellRq;
import com.midasit.midascafe.service.CellService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import javax.validation.Valid;

@Api(tags = "Cell Controller")
@RestController
@RequestMapping("cell")
@RequiredArgsConstructor  // 생성자 주입
public class CellController {

    private final CellService cellService;

    @Operation(summary = "셀 등록", description = "새로운 셀을 등록합니다.")
    @PostMapping
    public ResponseEntity<String> registerCell(@RequestBody @Valid RegisterCellRq registerCellRq) {
        int statusCode = cellService.registerCell(registerCellRq);

        if (statusCode == 201) {
            return ResponseEntity.ok("셀 등록 성공");
        } else if (statusCode == 409) {
            return new ResponseEntity<>("이미 등록된 셀입니다.", HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>("셀 등록에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "셀 목록 조회", description = "모든 셀 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<String>> getCellList() {
        return new ResponseEntity<>(cellService.getCellList(), HttpStatus.OK);
    }

    @Operation(summary = "셀 구성원 목록", description = "셀 구성원 목록을 조회합니다.")
    @GetMapping("/{name}/member")
    public ResponseEntity getMemberList(@PathVariable("name") String name) {
        JSONArray list = cellService.getMemberList(name);
        if(list == null) {
            return new ResponseEntity<>("해당 셀 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
    }

    @Operation(summary = "셀 이름 변경", description = "셀 이름을 변경합니다.")
    @PatchMapping("/{name}")
    public ResponseEntity modifyCellName(@PathVariable("name") String name, @RequestBody @Valid ModifyCellNameRq modifyCellNameRq) {
        int statusCode = cellService.modifyCellName(name, modifyCellNameRq);
        if (statusCode == 200) {
            return ResponseEntity.ok("셀 이름 변경 성공");
        } else if (statusCode == 404) {
            return new ResponseEntity<>("해당 셀 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } else if (statusCode == 409) {
            return new ResponseEntity<>("이미 존재하는 이름입니다.", HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>("셀 이름 변경에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "셀 삭제", description = "셀을 삭제합니다.")
    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteCell(@PathVariable("name") String name) {
        int statusCode = cellService.deleteCell(name);
        if (statusCode == 200) {
            return ResponseEntity.ok("셀 삭제 성공");
        } else if (statusCode == 403) {
            return new ResponseEntity<>("해당 셀에 멤버가 남아있습니다.", HttpStatus.FORBIDDEN);
        } else if (statusCode == 404) {
            return new ResponseEntity<>("해당 셀 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("셀 삭제에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }
}
