package com.midasit.midascafe.controller;

import com.midasit.midascafe.controller.rqrs.PayOrderRq;
import com.midasit.midascafe.dto.ResponseData;
import com.midasit.midascafe.service.PayService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "Root Controller")
@RestController
@RequiredArgsConstructor  // 생성자 주입
public class RootController {
    private final PayService payService;
    @Operation(summary = "졸음 방지용", description = "서버 졸음 방지")
    @GetMapping("/caffeine")
    public ResponseEntity<Void> caffeine() {
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "음료 주문", description = "주문을 취합하여 음료를 주문합니다.")
    @PostMapping("/fake-pay")
    public ResponseEntity<String> fakePay(@RequestBody @Valid PayOrderRq payOrderRq) {
        ResponseData responseData = payService.fakePay(payOrderRq);
        int statusCode = responseData.getStatusCode();
        if (statusCode == 200) {
            return ResponseEntity.ok(responseData.getResponseData());
        } else if (statusCode == 404) {
            return new ResponseEntity<>(responseData.getResponseData() , HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("주문에 실패하였습니다.\n" + responseData.getResponseData(), HttpStatus.valueOf(statusCode));
        }
    }
}
