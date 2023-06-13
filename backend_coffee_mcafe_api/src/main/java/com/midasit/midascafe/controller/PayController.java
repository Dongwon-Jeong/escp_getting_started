package com.midasit.midascafe.controller;

import com.midasit.midascafe.controller.rqrs.PayOrderRq;
import com.midasit.midascafe.dto.ResponseData;
import com.midasit.midascafe.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("pay")
@RequiredArgsConstructor  // 생성자 주입
public class PayController {

    private final PayService payService;
    @Operation(summary = "음료 주문", description = "주문을 취합하여 음료를 주문합니다.")
    @PostMapping
    public ResponseEntity<String> payOrder(@RequestBody @Valid PayOrderRq payOrderRq) {
        ResponseData responseData = payService.payOrder(payOrderRq);
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
