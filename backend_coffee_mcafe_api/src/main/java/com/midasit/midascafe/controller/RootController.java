package com.midasit.midascafe.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "Root Controller")
@RestController
public class RootController {
    @Operation(summary = "졸음 방지용", description = "서버 졸음 방지")
    @GetMapping("/caffeine")
    public ResponseEntity caffeine() {
        return ResponseEntity.ok(null);
    }
}
