package com.midasit.midascafe.controller;

import com.midasit.midascafe.controller.rqrs.OrderRs;
import com.midasit.midascafe.controller.rqrs.RegisterOrderRq;
import com.midasit.midascafe.dto.Order;
import com.midasit.midascafe.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Order Controller")
@RestController
@RequestMapping("order")
@RequiredArgsConstructor  // 생성자 주입
public class OrderController {
    private final OrderService orderService;
    @Operation(summary = "주문 등록", description = "새로운 주문을 등록합니다.")
    @PostMapping
    public ResponseEntity<String> registerOrder(@RequestBody @Valid RegisterOrderRq registerOrderRq) {
        HttpStatus statusCode = orderService.registerOrder(registerOrderRq);

        if (statusCode == HttpStatus.OK) {
            return ResponseEntity.ok("주문 등록 성공");
        } else if (statusCode == HttpStatus.UNAUTHORIZED) {
            return new ResponseEntity<>("인증에 실패하였습니다.", HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>("주문 등록에 실패하였습니다.", statusCode);
        }
    }

    @Operation(summary = "주문 삭제", description = "기존 주문을 삭제합니다.")
    @DeleteMapping(value = "/{orderId}")
    public ResponseEntity<String> deleteOrder(@RequestHeader HttpHeaders headers, @PathVariable(value = "orderId") String orderId) {
        String authorizationValue = headers.getFirst("Authorization");
        if (authorizationValue == null) {
            return new ResponseEntity<>("전화번호와 암호를 확인해주세요." , HttpStatus.UNAUTHORIZED);
        }
        HttpStatus statusCode = orderService.deleteOrder(authorizationValue, orderId);

        if (statusCode == HttpStatus.OK) {
            return ResponseEntity.ok("주문 삭제 성공");
        } else if (statusCode == HttpStatus.NOT_FOUND) {
            return new ResponseEntity<>("주문을 찾을 수 없습니다." , HttpStatus.NOT_FOUND);
        } else if (statusCode == HttpStatus.UNAUTHORIZED) {
            return new ResponseEntity<>("전화번호와 암호를 확인해주세요." , HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>("주문 삭제에 실패하였습니다.", statusCode);
        }
    }

    @Operation(summary = "그룹의 주문 목록", description = "해당 그룹의 주문 목록을 조회합니다.")
    @GetMapping(value = "/{phone}")
    public ResponseEntity<List<OrderRs>> getOrderList(@PathVariable(value = "phone") String phone) {
        return ResponseEntity.ok(null);
    }
}
