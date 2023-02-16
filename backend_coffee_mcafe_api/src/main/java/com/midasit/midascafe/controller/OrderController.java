package com.midasit.midascafe.controller;

import com.midasit.midascafe.controller.rqrs.RegisterOrderRq;
import com.midasit.midascafe.dto.Order;
import com.midasit.midascafe.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
        int statusCode = orderService.registerOrder(registerOrderRq);

        if (statusCode == 201) {
            return ResponseEntity.ok("주문 등록 성공");
        } else if (statusCode == 404) {
            return new ResponseEntity<>("해당 사용자를 찾을 수 없습니다." , HttpStatus.NOT_FOUND);
        } else if (statusCode == 409) {
            return new ResponseEntity<>("이미 주문이 있습니다.", HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>("주문 등록에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "주문 삭제", description = "기존 주문을 삭제합니다.")
    @DeleteMapping(value = "/{phone}")
    public ResponseEntity<String> deleteOrder(@PathVariable(value = "phone") String phone) {
        int statusCode = orderService.deleteOrder(phone);

        if (statusCode == 200) {
            return ResponseEntity.ok("주문 삭제 성공");
        } else if (statusCode == 404) {
            return new ResponseEntity<>("주문을 찾을 수 없습니다." , HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("주문 삭제에 실패하였습니다.", HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "셀의 주문 목록", description = "해당 셀의 주문 목록을 조회합니다.")
    @GetMapping(value = "/{cell}")
    public ResponseEntity<List<Order>> getOrderList(@PathVariable(value = "cell") String cellName) {
        return ResponseEntity.ok(orderService.getOrderListByPhone(cellName));
    }
}
