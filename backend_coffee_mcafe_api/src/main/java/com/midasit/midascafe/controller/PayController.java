package com.midasit.midascafe.controller;

import com.midasit.midascafe.controller.rqrs.PayOrderRq;
import com.midasit.midascafe.service.PayService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "Pay Controller")
@RestController
@RequestMapping("pay")
@RequiredArgsConstructor  // 생성자 주입
public class PayController {

    private final PayService payService;
    @Operation(summary = "음료 주문", description = "주문을 취합하여 음료를 주문합니다.")
    @PostMapping
    public ResponseEntity<String> payOrder(@RequestBody @Valid PayOrderRq payOrderRq) {
        int statusCode = payService.payOrder(payOrderRq);

        if (statusCode == 201) {
            return ResponseEntity.ok("주문 성공");
        } else if (statusCode == 404) {
            return new ResponseEntity<>("해당 사용자를 찾을 수 없습니다." , HttpStatus.NOT_FOUND);
        } else if (statusCode == 409) {
            return new ResponseEntity<>("이미 주문이 있습니다.", HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>("주문 등록에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }
}
