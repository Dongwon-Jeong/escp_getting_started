package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterOrderRq;
import com.midasit.midascafe.dto.Order;

import java.util.List;

public interface OrderService {
    int registerOrder(RegisterOrderRq registerOrderRq);
    int deleteOrder(String phone);
    boolean hasOrder(String phone);
    List<Order> getOrderList(String cellName);
}
