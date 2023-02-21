package com.midasit.midascafe.controller;


import com.midasit.midascafe.controller.rqrs.ModifyGroupNameRq;
import com.midasit.midascafe.controller.rqrs.RegisterGroupRq;
import com.midasit.midascafe.service.GroupService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import javax.validation.Valid;

@Api(tags = "Group Controller")
@RestController
@RequestMapping("group")
@RequiredArgsConstructor  // 생성자 주입
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "그룹 등록", description = "새로운 그룹을 등록합니다.")
    @PostMapping
    public ResponseEntity<String> registerGroup(@RequestBody @Valid RegisterGroupRq registerGroupRq) {
        int statusCode = groupService.registerGroup(registerGroupRq);

        if (statusCode == 201) {
            return ResponseEntity.ok("그룹 등록 성공");
        } else if (statusCode == 409) {
            return new ResponseEntity<>("이미 등록된 그룹입니다.", HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>("그룹 등록에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "그룹 목록 조회", description = "모든 그룹 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<String>> getGroupList() {
        return new ResponseEntity<>(groupService.getGroupList(), HttpStatus.OK);
    }

    @Operation(summary = "그룹 구성원 목록", description = "그룹 구성원 목록을 조회합니다.")
    @GetMapping("/{name}/member")
    public ResponseEntity getMemberList(@PathVariable("name") String name) {
        JSONArray list = groupService.getMemberList(name);
        if(list == null) {
            return new ResponseEntity<>("해당 그룹 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
    }

    @Operation(summary = "그룹 이름 변경", description = "그룹 이름을 변경합니다.")
    @PatchMapping("/{name}")
    public ResponseEntity modifyGroupName(@PathVariable("name") String name, @RequestBody @Valid ModifyGroupNameRq modifyGroupNameRq) {
        int statusCode = groupService.modifyGroupName(name, modifyGroupNameRq);
        if (statusCode == 200) {
            return ResponseEntity.ok("그룹 이름 변경 성공");
        } else if (statusCode == 404) {
            return new ResponseEntity<>("해당 그룹 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } else if (statusCode == 409) {
            return new ResponseEntity<>("이미 존재하는 이름입니다.", HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>("그룹 이름 변경에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "그룹 삭제", description = "그룹을 삭제합니다.")
    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteGroup(@PathVariable("name") String name) {
        int statusCode = groupService.deleteGroup(name);
        if (statusCode == 200) {
            return ResponseEntity.ok("그룹 삭제 성공");
        } else if (statusCode == 403) {
            return new ResponseEntity<>("해당 그룹에 멤버가 남아있습니다.", HttpStatus.FORBIDDEN);
        } else if (statusCode == 404) {
            return new ResponseEntity<>("해당 그룹 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("그룹 삭제에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }
}
