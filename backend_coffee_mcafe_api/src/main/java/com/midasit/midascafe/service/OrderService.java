package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterOrderRq;
import com.midasit.midascafe.dto.Order;
import org.springframework.http.HttpStatus;



public interface OrderService {
    HttpStatus registerOrder(RegisterOrderRq registerOrderRq);

    Order getOrder(String orderId);

    HttpStatus deleteOrder(String authorizationValue, String orderId);
}
